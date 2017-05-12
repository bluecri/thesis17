package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.Toast;

/**
 * Created by kimz on 2017-05-07.
 */

public class CustomWeekView extends View {
    public static int COL_NUM = 7;
    public static int ROW_NUM = 24;

    static int MODE_NONE = 0, MODE_DRAG_INIT = 1, MODE_DRAG_HOR = 2, MODE_DRAG_VER = 3, MODE_PAN = 4;

    private Paint paint;
    private int canvasColNum, canvasRowNum;  //number of canvas Col, Row
    private int subRowNum; //number of subRow in 1 Row
    private float fMaxBlockCol, fMinBlockCol, fMaxBlockRow, fMinBlockRow; //min, max size of each block
    private int customViewWidth, customViewHeight;  //px size of CustomView
    private float fLeftSideSpace, fUpSideSpace;		//px size of left, up side space
    private Points pCurScrollLeftUp;   //스크롤시 좌상단 좌표
    private Points pCurBlock;   //current size of block
    private float fCustomViewWidthExceptSpace, fCustomViewHeightExceptSpace;		//space제외한 순수 block들의 인쇄 공간 size
    private float fScrollRightEnd, fScrollBottomEnd;	//scroll의 끝 부분.

    //touch evnet x, y
    private float[] touchX = new float[2];
    private float[] touchY = new float[2];
    private int[] touchId = new int[2];
    private int touchMode;	//0 = cancel, 4 = draginit, 1 = Drag horizontal, 2=drag vertical, 3.pan
    long startClickTime = 0;

    public CustomWeekView(Context context) {
        super(context);

        paint = new Paint();
        //init();
    }

    public CustomWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        //init();
    }


    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        refreshInit();  //curBlock 변경에 따른 scroll의 제한 위치 갱신
        drawAllBaseBlock(canvas);
        //canvas.drawRect(0,0,200,200, paint);

    }


    public void init(){
        //setCustomViewWidthHeight(); //setting CustomView size
        fLeftSideSpace = 30f;//(float)customViewWidth/10;		//setting left side empty space
        fUpSideSpace = 40f;//(float)customViewHeight/20;
        fCustomViewHeightExceptSpace = (float)customViewHeight - fUpSideSpace;
        fCustomViewWidthExceptSpace = (float)customViewWidth - fLeftSideSpace;
        setCanvasBlock(7, 24, 2);   //setting week block
        //초기 블럭의 size setting(min)
        pCurBlock = new Points(fMinBlockCol, fMinBlockRow);
        pCurScrollLeftUp = new Points(0, 0);

        //초기 스크롤 setting
        pCurScrollLeftUp.setFPointColRow(0, 0);
        fScrollRightEnd = COL_NUM * pCurBlock.fCol - fCustomViewWidthExceptSpace;
        fScrollBottomEnd = ROW_NUM * pCurBlock.fRow - fCustomViewHeightExceptSpace;
        Log.d("block", customViewHeight  +" "+ customViewWidth);
        Log.d("block", fLeftSideSpace  +" "+ fUpSideSpace  +" "+ fCustomViewHeightExceptSpace  +" "+ fCustomViewWidthExceptSpace  +" "+ pCurScrollLeftUp.fCol  +" "+ pCurScrollLeftUp.fRow);
    }

    public void refreshInit(){
        fScrollRightEnd = COL_NUM * pCurBlock.fCol - fCustomViewWidthExceptSpace;
        fScrollBottomEnd = ROW_NUM * pCurBlock.fRow - fCustomViewHeightExceptSpace;
    }

    //현재 customView의 높이 너비 크기를 px size로 받는다. View가 생성된 뒤 호출할 것.
    public void setCustomViewWidthHeight(){
        customViewHeight = getWidth();
        customViewWidth = getHeight();
    }

    //canvas를 블럭으로 나눌 기준.
    public void setCanvasBlock(int col, int row, int subrow) {
        canvasColNum = 7;
        canvasRowNum = 24;
        subRowNum = 2;
        //블럭의 최대 사이즈
        fMaxBlockRow = (float) fCustomViewHeightExceptSpace / 3;
        fMaxBlockCol = (float) fCustomViewWidthExceptSpace / 3;
        //블럭의 최소 사이즈
        fMinBlockRow = (float) fCustomViewHeightExceptSpace / 15;
        fMinBlockCol = (float) fCustomViewWidthExceptSpace / 7;
    }



    public void drawAllBaseBlock(Canvas canvas){
        int iRowStartIdx = (int)Math.floor(pCurScrollLeftUp.fRow/pCurBlock.fRow);       //row의 시작 index.
        float fRowposition = (iRowStartIdx + 1) * pCurBlock.fRow - pCurScrollLeftUp.fRow + fUpSideSpace;   //row 시작 위치(px). 블럭의 크기 - 스크롤 위치 + 상위 space 높이
        float fAcc;  //subline만큼씩 위치 증가
        int iRowEndIdx = (int)Math.floor(fCustomViewHeightExceptSpace/pCurBlock.fRow)+ iRowStartIdx;     //row 마지막 줄 index. (스크롤높이 + view 높이)/블록높이 + 시작 index

        int iColStartIdx = (int)Math.floor(pCurScrollLeftUp.fCol/pCurBlock.fCol);       //col의 시작 index.
        float fColposition = (iColStartIdx + 1) * pCurBlock.fCol - pCurScrollLeftUp.fCol + fLeftSideSpace;   //col 시작 위치(px)
        int iColEndIdx = (int)Math.floor(fCustomViewWidthExceptSpace/pCurBlock.fCol)+ iColStartIdx;     //col 마지막 줄 index. (스크롤높이 + view 높이)/블록높이 + 시작 index
        //customViewHeightExceptSpace/pCurBlock.fcol + 1은 화면에 나타나는 줄의 개수와 같다.

        float[] arrFBoldLines = new float[(iRowEndIdx - iRowStartIdx + 1)*4 + (iColEndIdx - iColStartIdx + 1)*4 + 8];   //col line, row line, main UP&LEFT space border line(8)
        float[] arrFThinLines = new float[(iRowEndIdx - iRowStartIdx + 1)*(subRowNum-1)*4 + 4]; //sublines Num * 4 + (first subline) * 4

        int iArrFBoldLinesIndex = 0;
        int iArrFThinLinesIndex = 0;

        if(subRowNum != 0)
            fAcc = pCurBlock.fRow/subRowNum;
        else
            fAcc = pCurBlock.fRow;
        //Log.d("block", "upspace, leftspace : " + fUpSideSpace + " " + fLeftSideSpace);
        Log.d("block", iRowStartIdx +" "+ iRowEndIdx  +" "+ iColStartIdx  +" "+ iColEndIdx);
        //Log.d("block", "rowposition, colposition " + fRowposition  +" "+ fColposition);
        //Log.d("block", "rowblocksize, colblucksize " + pCurBlock.fRow + " " + pCurBlock.fCol);
        //Log.d("block", "fAcc = " + fAcc);

        //draw horizontal lines
        //draw first subline
        arrFThinLines[iArrFThinLinesIndex++] = 0;
        arrFThinLines[iArrFThinLinesIndex++] = fRowposition - fAcc;
        arrFThinLines[iArrFThinLinesIndex++] = customViewWidth;
        arrFThinLines[iArrFThinLinesIndex++] = fRowposition - fAcc;


        for(int rowLines = iRowStartIdx; rowLines <= iRowEndIdx; rowLines++){    //draw main lines
            //draw Main line in fRowposition

            //start point of line
            arrFBoldLines[iArrFBoldLinesIndex++] = 0;
            arrFBoldLines[iArrFBoldLinesIndex++] = fRowposition;
            //end point of line
            arrFBoldLines[iArrFBoldLinesIndex++] = customViewWidth;
            arrFBoldLines[iArrFBoldLinesIndex++] = fRowposition;
            //canvas.drawLine(0, arrFBoldLines[iArrFBoldLinesIndex-3], customViewWidth, arrFBoldLines[iArrFBoldLinesIndex-1], paint);
            fRowposition += (fAcc);
            for(int subRowLines = 0; subRowLines < subRowNum-1; subRowLines++){ //draw sublines
                //draw Subline in fRowPosition
                //paint.setStrokeWidth(fThinLine);
                arrFThinLines[iArrFThinLinesIndex++] = 0;
                arrFThinLines[iArrFThinLinesIndex++] = fRowposition;
                //end point of line
                arrFThinLines[iArrFThinLinesIndex++] = customViewWidth;
                arrFThinLines[iArrFThinLinesIndex++] = fRowposition;
                //Log.d("block", "subline " + arrFThinLines[iArrFThinLinesIndex-4]+ " " + arrFThinLines[iArrFThinLinesIndex-3] + " "  + arrFThinLines[iArrFThinLinesIndex-2] + " " + arrFThinLines[iArrFThinLinesIndex-1]);
                fRowposition += (fAcc);
            }
        }

        fAcc = pCurBlock.fCol;  //line만큼씩 위치 증가

        //draw vertical lines
        for(int colLines = iColStartIdx; colLines <= iColEndIdx; colLines++){    //draw main lines
            //draw Main line in fColposition

            //start point of line
            arrFBoldLines[iArrFBoldLinesIndex++] = fColposition;
            arrFBoldLines[iArrFBoldLinesIndex++] = 0;
            //end point of line
            arrFBoldLines[iArrFBoldLinesIndex++] = fColposition;
            arrFBoldLines[iArrFBoldLinesIndex++] = customViewHeight;

            fColposition += (fAcc);

        }

        //draw Left & Up border
        arrFBoldLines[iArrFBoldLinesIndex++] = 0;
        arrFBoldLines[iArrFBoldLinesIndex++] = fUpSideSpace;
        arrFBoldLines[iArrFBoldLinesIndex++] = customViewWidth;
        arrFBoldLines[iArrFBoldLinesIndex++] = fUpSideSpace;

        arrFBoldLines[iArrFBoldLinesIndex++] = fLeftSideSpace;
        arrFBoldLines[iArrFBoldLinesIndex++] = 0;
        arrFBoldLines[iArrFBoldLinesIndex++] = fLeftSideSpace;
        arrFBoldLines[iArrFBoldLinesIndex++] = customViewHeight;


        float fBoldLineStroke = 2.0f;     //굵은 라인
        float fThinLineStroke = 1.0f;     //얇은 라인   반드시 1 이상!
        paint.setColor(Color.BLACK);    //line color black
        paint.setStrokeWidth(fThinLineStroke);
        canvas.drawLines(arrFThinLines, paint);

        paint.setStrokeWidth(fBoldLineStroke);
        canvas.drawLines(arrFBoldLines, paint);


        /*for(int i=0; i<arrFBoldLines.length; i++){
            Log.d("block", "" + arrFBoldLines[i]);
        }*/


    }

    public void drawAllItemBlock(){
        //print - iColStartIdx - print --------- print - iColEndIdx - print
        //for(int col = iColStartIdx; col <)
        //    return;


    }



    public boolean onTouchEvent(MotionEvent event){
        int maskAction = event.getAction() & MotionEvent.ACTION_MASK;

        if (maskAction == MotionEvent.ACTION_DOWN) {
            Log.d("block", "ACTION_DOWN");
            touchX[0] = event.getX();
            touchY[0] = event.getY();	//get coordinates
            touchId[0] = event.getPointerId(0);

            startClickTime = System.currentTimeMillis();

            touchMode = MODE_DRAG_INIT;

        }
        //2 points 이상 존재시 2개의 포인트에 해당하는 좌표 얻기
        else if(maskAction == MotionEvent.ACTION_POINTER_DOWN){
            Log.d("block", "ACTION_POINTER_DOWN");
			/*int pointerCnt = event.getPointerCount();	//get point count
			if(pointerCnt >2)
				pointerCnt = 2;*/		//fix count to 2
            touchX[0] = event.getX(0);
            touchY[0] = event.getY(0);
            touchId[0] = event.getPointerId(0);
            touchX[1] = event.getX(1);
            touchY[1] = event.getY(1);
            touchId[1] = event.getPointerId(1);
            touchMode = MODE_PAN;

        }
        else if(maskAction == MotionEvent.ACTION_MOVE){

            float moveX = event.getX(0);
            float moveY = event.getY(0);
            Log.d("block", "ACTION_MOV");
            //check DRAG Vertival or Horizontal
            if(touchMode == MODE_DRAG_INIT){
                Log.d("block", "ACTION_MOVE init");
                if(Math.abs(moveX-touchX[0]) <= Math.abs(moveY-touchY[0])){	//y방향 변화량이 큰 경우 vertical scroll
                    touchMode = MODE_DRAG_VER;
                }
                else{
                    touchMode = MODE_DRAG_HOR;
                }
            }

            if(touchMode == MODE_DRAG_HOR){
                Log.d("block", "ACTION_MOVE drag hor");
                //move scroll  += MoveX - touchX[0]
                pCurScrollLeftUp.fCol -= (moveX - touchX[0]);   //move 방향과 반대로 화면을 이동해야 한다.
                touchX[0] = moveX;  //변화한 좌표로 기존의 좌표를 바꿈.
                if(pCurScrollLeftUp.fCol > fScrollRightEnd){
                    pCurScrollLeftUp.fCol = fScrollRightEnd;    //스크롤의 상한선까지(정해놓은 column만 인쇄하도록)
                    Log.d("block", "vlue " + fScrollRightEnd);
                }
                else if(pCurScrollLeftUp.fCol < 0){
                    pCurScrollLeftUp.fCol = 0;
                }


            }
            else if(touchMode == MODE_DRAG_VER){
                Log.d("block", "ACTION_MOVE drag vert");
                pCurScrollLeftUp.fRow -= (moveY - touchY[0]);
                touchY[0] = moveY;
                if(pCurScrollLeftUp.fRow > fScrollBottomEnd){
                    pCurScrollLeftUp.fRow = fScrollBottomEnd;
                }
                else if(pCurScrollLeftUp.fRow < 0){
                    pCurScrollLeftUp.fRow = 0;
                }
            }
            else if(touchMode == MODE_PAN){
                Log.d("block", "ACTION_MOVE pan");
                float moveX2 = event.getX(1);
                float moveY2 = event.getY(1);
                float centerX, centerY, detX, detY;	//center 변화량, xy 변화량
                centerX = (moveX+moveX2)/2 -(touchX[0]+touchX[1])/2;
                centerY = (moveY+moveY2)/2 - (touchY[0]+touchY[1])/2;
                detX = Math.abs(moveX-moveX2) - Math.abs(touchX[0]-touchX[1]);
                detY = Math.abs(moveY-moveY2) - Math.abs(touchY[0]-touchY[1]);

                //move scroll vertical
                pCurScrollLeftUp.fRow -= (centerY);
                if(pCurScrollLeftUp.fRow > fScrollBottomEnd){
                    pCurScrollLeftUp.fRow = fScrollBottomEnd;
                }
                else if(pCurScrollLeftUp.fRow < 0){
                    pCurScrollLeftUp.fRow = 0;
                }
                //move scroll horizontal
                pCurScrollLeftUp.fCol -= (centerX);
                if(pCurScrollLeftUp.fCol > fScrollRightEnd){
                    pCurScrollLeftUp.fCol = fScrollRightEnd;
                }
                else if(pCurScrollLeftUp.fCol < 0){
                    pCurScrollLeftUp.fCol = 0;
                }

                //change sizeof box
                pCurBlock.fCol += detX/2;
                if(pCurBlock.fCol > fMaxBlockCol){
                    pCurBlock.fCol = fMaxBlockCol;
                }
                else if(pCurBlock.fCol < fMinBlockCol){
                    pCurBlock.fCol = fMinBlockCol;
                }

                pCurBlock.fRow += detY/2;
                if(pCurBlock.fRow > fMaxBlockRow){
                    pCurBlock.fRow = fMaxBlockRow;
                }
                else if(pCurBlock.fRow < fMinBlockRow){
                    pCurBlock.fRow = fMinBlockRow;
                }

                touchX[0]= moveX;
                touchY[0] = moveY;
                touchX[1] = moveX2;
                touchY[1] = moveY2;
            }

        }
        else if (maskAction == MotionEvent.ACTION_UP) {
            //TODO ViewConfiguration ??
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {

                // Touch was a simple tap. Do whatever.	Click
                Toast.makeText(super.getContext(), "click : " + event.getX() + ", " + event.getY() + ", " + getWidth() + ", " + getHeight(), Toast.LENGTH_LONG).show();
                Log.d("block", "ACTION_UP click");

            } else {

                // 오래 눌렀다 손을 놓는 경우 아무 처리하지 않음?
                Log.d("block", "ACTION_UP long click");
            }
            touchMode = MODE_NONE;

        }
        else if(maskAction == MotionEvent.ACTION_POINTER_UP){
            Log.d("block", "ACTION_POINTER_UP");
            touchMode = MODE_DRAG_INIT;
            //todo which one is remain.
            /*touchX[0] = event.getX();
            touchY[0] = event.getY();
            touchId[0] = event.getPointerId();
            */
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //Log.d("block", "ACTION_DOWN");
            //Toast.makeText(super.getContext(), "pos : " + event.getX() + ", " + event.getY() + ", " + getWidth() + ", " + getHeight(), Toast.LENGTH_LONG).show();
        }

        //todo invalidate();
        invalidate();
        return true;
        //return super.onTouchEvent(event);
    }

    /*
    뷰가 생성된 뒤 뷰의 크기를 알아내기 위해 view 생성 뒤 호출되는 getViewTreeObserver에 함수를 등록한다.
    onCreate, inflater등의 함수에서는 아직 뷰가 완전히 생성된 것이 아니므로 아래를 통해 getX, getY해야 한다.
     */

    //뷰의 크기가 변경된 경우 호출됨
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //view의 layout 위치를 정한다. param들은 App의 전체화면을 기준으로 위치정보를 담고있다.
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //custom View를 생성하고 XML에 정의된 View의 fixed size를 가져오거나 match_parent, fill_parent와 같이 외부상황으로 인해 정해진 size를 얻어내어 view의 크기를 정한다.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //set Height, Width of custom View
        if(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY){
            customViewHeight = MeasureSpec.getSize(heightMeasureSpec);
            customViewWidth = MeasureSpec.getSize(widthMeasureSpec);
            //Log.d("block", customViewHeight +" " + customViewWidth);
            init();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}