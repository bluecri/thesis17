package com.sample.thesis17.mytimeapp.baseCalendar.week;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.sample.thesis17.mytimeapp.baseTimeTable.Points;

import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Created by kimz on 2017-09-21.
 */

//Equal to CustomWeekView
public class CalenderWeekView extends View {
    public Context curContext;

    private static int COL_NUM = 7;
    private static int ROW_NUM = 24;

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

    //db
    CalenderWeekAdapter curCustomWeekAdapter = null;
    List<CalenderWeekItem> customItemList = null;

    public CalenderWeekView(Context context) {
        super(context);
        curContext = context;

        paint = new Paint();
        //init();
    }

    public CalenderWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        curContext = context;
        paint = new Paint();
        //init();
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        refreshInit();  //curBlock 변경에 따른 scroll의 제한 위치 갱신
        drawAllBaseBlock(canvas);
        drawTimeAndWeek(canvas);
        drawContentBlocks(canvas);
    }

    public void init(){
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
        if(curCustomWeekAdapter == null){
            Log.d("block", "curCustomWeekAdapter null");
        }
        curCustomWeekAdapter.setConfig(fUpSideSpace, fLeftSideSpace, fCustomViewWidthExceptSpace, fCustomViewHeightExceptSpace);
    }

    public void refreshInit(){
        //Log.d("debbugged", "refreshInit()");
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

    public void drawTimeAndWeek(Canvas canvas){
        Paint fontPaint = new Paint();
        fontPaint.setAntiAlias(true);   //aliasing

        //draw week
        fontPaint.setColor(Color.RED);
        fontPaint.setTextSize(20);
        String[] strArrWeek = {"일", "월", "화", "수", "목", "금", "토"};
        float alignOfWeek = pCurBlock.fCol;   //간격
        float startPos = -pCurScrollLeftUp.fCol + pCurBlock.fCol/2 + fLeftSideSpace;

        if(startPos >= fLeftSideSpace)  //왼쪽 빈 공간을 침범하지 않는 경우에만.
            canvas.drawText(strArrWeek[0], startPos, 20, fontPaint);        //draw "일"
        startPos += pCurBlock.fCol;

        fontPaint.setColor(Color.BLACK);
        for(int i=1; i<6 ; i++){
            if(startPos >= fLeftSideSpace)
                canvas.drawText(strArrWeek[i], startPos, 20, fontPaint);
            startPos += pCurBlock.fCol;
        }

        fontPaint.setColor(Color.BLUE);
        if(startPos >= fLeftSideSpace)
            canvas.drawText(strArrWeek[6], startPos, 20, fontPaint);        //draw "일"

        //draw time
        fontPaint.setColor(Color.BLACK);
        startPos = -pCurScrollLeftUp.fRow + fUpSideSpace + 15;
        for(int i=0; i<24; i++){
            canvas.drawText("" + i, 9, startPos, fontPaint);
            startPos += pCurBlock.fRow;
        }
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

        //draw horizontal lines : first subline
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
            fRowposition += (fAcc);
            for(int subRowLines = 0; subRowLines < subRowNum-1; subRowLines++){ //draw sublines
                //draw Subline in fRowPosition
                //paint.setStrokeWidth(fThinLine);
                arrFThinLines[iArrFThinLinesIndex++] = 0;
                arrFThinLines[iArrFThinLinesIndex++] = fRowposition;
                //end point of line
                arrFThinLines[iArrFThinLinesIndex++] = customViewWidth;
                arrFThinLines[iArrFThinLinesIndex++] = fRowposition;
                fRowposition += (fAcc);
            }
        }
        fAcc = pCurBlock.fCol;  //line만큼씩 위치 증가

        //draw vertical lines
        for(int colLines = iColStartIdx; colLines <= iColEndIdx; colLines++){    //draw main lines
            //draw Main line in fColposition start point of line
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
    }

    //touch 좌표로 현재 몇번 block인지 확인.
    public float getfCurBlockCol(float clickedCol){
        if(clickedCol < fLeftSideSpace)
            return 0;
        return (pCurScrollLeftUp.fCol+clickedCol-fLeftSideSpace)/pCurBlock.fCol;
    }

    public float getfCurBlockRow(float clickedRow){
        if(clickedRow < fUpSideSpace)
            return 0;
        return (pCurScrollLeftUp.fRow+clickedRow-fUpSideSpace)/pCurBlock.fRow;
    }

    public boolean onTouchEvent(MotionEvent event){
        boolean isPCurBlockModified = true;
        int maskAction = event.getAction() & MotionEvent.ACTION_MASK;

        if (maskAction == MotionEvent.ACTION_DOWN) {
            touchX[0] = event.getX();
            touchY[0] = event.getY();	//get coordinates
            touchId[0] = event.getPointerId(0);

            startClickTime = System.currentTimeMillis();
            touchMode = MODE_DRAG_INIT;
        }
        //2 points 이상 존재시 2개의 포인트에 해당하는 좌표 얻기
        else if(maskAction == MotionEvent.ACTION_POINTER_DOWN){
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

            //check DRAG Vertival or Horizontal
            if(touchMode == MODE_DRAG_INIT){
                if(Math.abs(moveX-touchX[0]) <= Math.abs(moveY-touchY[0])){	//y방향 변화량이 큰 경우 vertical scroll
                    touchMode = MODE_DRAG_VER;
                }
                else{
                    touchMode = MODE_DRAG_HOR;
                }
                touchX[0]= moveX;
                touchY[0] = moveY;
            }

            if(touchMode == MODE_DRAG_HOR){
                //move scroll  += MoveX - touchX[0]
                pCurScrollLeftUp.fCol -= (moveX - touchX[0]);   //move 방향과 반대로 화면을 이동해야 한다.
                touchX[0] = moveX;  //변화한 좌표로 기존의 좌표를 바꿈.
                if(pCurScrollLeftUp.fCol > fScrollRightEnd){
                    pCurScrollLeftUp.fCol = fScrollRightEnd;    //스크롤의 상한선까지(정해놓은 column만 인쇄하도록)
                }
                else if(pCurScrollLeftUp.fCol < 0){
                    pCurScrollLeftUp.fCol = 0;
                }


            }
            else if(touchMode == MODE_DRAG_VER){
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
                //Log.d("fanblock", "ACTION_MOVE pan");
                //ACTION_MOVE에서 moveX1, Y1 받음
                float moveX1 = event.getX(1);
                float moveY1 = event.getY(1);
                float centerX, centerY, detX, detY;	//center 변화량, xy 변화량
                //Log.d("fanblock", "touchX[0] : " + touchX[0] + " " + "touchX[1] : " + touchX[1] + " " + "moveX : " + moveX + " " + "moveX1 : " + moveX1 +"moveY11 : " + moveY+  " moveY2 : " + moveY1);
                //Log.d("fanblock", "getfCurBlockCol[0] : " + getfCurBlockCol(touchX[0]) + " " + "getfCurBlockRow[1] : " + getfCurBlockRow(touchX[1]));
                if(!(abs(moveX1 - moveX) < fMaxBlockCol) || (abs(moveY1 - moveY) < fMaxBlockRow)){
                    //get Det X
                    if(touchX[0] < touchX[1] && moveX < moveX1){
                        detX = touchX[0] - moveX + moveX1 - touchX[1];
                        //centerX = getfCurBlockCol(touchX[1]) * (touchX[0] - moveX) + getfCurBlockCol(touchX[1]) * ( moveX1 - touchX[1]);
                        centerX = getfCurBlockCol(touchX[1]) * (touchX[0] - moveX) + getfCurBlockCol(touchX[0]) * ( moveX1 - touchX[1]);
                    }
                    else{
                        detX = -moveX1 + touchX[1] - touchX[0] + moveX ;
                        //centerX = getfCurBlockCol(touchX[1]) * (- touchX[0] + moveX) + getfCurBlockCol(touchX[1]) * ( -moveX1 + touchX[1]);
                        centerX = getfCurBlockCol(touchX[1]) * (- touchX[0] + moveX) + getfCurBlockCol(touchX[0]) * ( -moveX1 + touchX[1]);
                    }
                    //modify block col with min max
                    detX /= 3;
                    if(pCurBlock.fCol + detX > fMaxBlockCol){
                        pCurBlock.fCol = fMaxBlockCol;
                    }
                    else if(pCurBlock.fCol + detX < fMinBlockCol){
                        pCurBlock.fCol = fMinBlockCol;
                    }
                    else{
                        pCurBlock.fCol = pCurBlock.fCol + detX;
                    }
                    //centerX
                    centerX /= 3;
                    pCurScrollLeftUp.fCol += centerX;
                    pCurScrollLeftUp.fCol = (pCurScrollLeftUp.fCol > fScrollRightEnd) ? fScrollRightEnd : pCurScrollLeftUp.fCol;
                    pCurScrollLeftUp.fCol = (pCurScrollLeftUp.fCol < 0) ? 0 : pCurScrollLeftUp.fCol;

                    //get Det Y
                    if(touchY[0] < touchY[1] && moveY < moveY1){
                        detY = touchY[0] - moveY + moveY1 - touchY[1];
                        centerY = getfCurBlockRow(touchY[1]) * (touchY[0] - moveY) + getfCurBlockRow(touchY[0]) * ( moveY1 - touchY[1]);
                    }
                    else{
                        detY = -moveY1 + touchY[1] - touchY[0] + moveY ;
                        centerY = getfCurBlockRow(touchY[1]) * (- touchY[0] + moveY) + getfCurBlockRow(touchY[0]) * ( -moveY1 + touchY[1]);
                    }

                    //modify block col with min max
                    detY /= 6;
                    if(pCurBlock.fRow + detY > fMaxBlockRow){
                        pCurBlock.fRow = fMaxBlockRow;
                    }
                    else if(pCurBlock.fRow + detY < fMinBlockRow){
                        pCurBlock.fRow = fMinBlockRow;
                    }
                    else{
                        pCurBlock.fRow = pCurBlock.fRow + detY;
                    }

                    //centerX
                    centerY /= 6;
                    pCurScrollLeftUp.fRow += centerY;
                    pCurScrollLeftUp.fRow = (pCurScrollLeftUp.fRow > fScrollBottomEnd) ? fScrollBottomEnd : pCurScrollLeftUp.fRow;
                    pCurScrollLeftUp.fRow = (pCurScrollLeftUp.fRow < 0) ? 0 : pCurScrollLeftUp.fRow;
                }
                touchX[0]= moveX;
                touchY[0] = moveY;
                touchX[1] = moveX1;
                touchY[1] = moveY1;
            }
        }
        else if (maskAction == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                // Touch was a simple tap. Do whatever.	Click
                Toast.makeText(super.getContext(), "click : " + event.getX() + ", " + event.getY() + ", " + getWidth() + ", " + getHeight(), Toast.LENGTH_LONG).show();
                //Click
                int retIdx = curCustomWeekAdapter.getIdxWithClicked(event.getX(), event.getY());
                if(retIdx == -1){
                    Log.d("block", "click box find error");
                }
                //Log.d("block", "ACTION_UP click");
            } else {
                // 오래 눌렀다 손을 놓는 경우 아무 처리하지 않음
                Log.d("block", "ACTION_UP long click");
            }
            touchMode = MODE_NONE;

        }
        else if(maskAction == MotionEvent.ACTION_POINTER_UP){
            touchMode = MODE_DRAG_INIT;
            touchX[0] = event.getX();
            touchY[0] = event.getY();
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //Log.d("block", "ACTION_DOWN");
            //Toast.makeText(super.getContext(), "pos : " + event.getX() + ", " + event.getY() + ", " + getWidth() + ", " + getHeight(), Toast.LENGTH_LONG).show();
        }

        invalidate();
        return true;
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
        //Log.d("debbugged", "onMesure");
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

    //adapter를 등록한다.
    public void setCustomWeekAdapter(CalenderWeekAdapter adapt){
        //adapt.updateCustomWeekItemList();
        curCustomWeekAdapter = adapt;
    }

    //customItemList로 block과 text를 draw 한다.
    public void drawContentBlocks(Canvas canvas){
        Log.d("draws", "drawContentBlocks()");
        //rect Paint
        Paint tempRectPaint= new Paint();
        tempRectPaint.setColor(Color.GREEN);

        //stroke paint
        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(2);
        int cornerRadius = 20;

        //stroke paint
        Paint strokeTempPaint = new Paint();
        strokeTempPaint.setStyle(Paint.Style.STROKE);
        strokeTempPaint.setColor(Color.YELLOW);
        strokeTempPaint.setStrokeWidth(2);

        //text Paint
        Paint tempTextPaint= new Paint();
        tempTextPaint.setColor(Color.BLACK);
        tempTextPaint.setAntiAlias(true);
        tempTextPaint.setTextAlign(Paint.Align.LEFT);
        tempTextPaint.setTextSize((float)20.0);
        Paint.FontMetrics fm = tempTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;      //use textHeight( < lineHeight)
        Log.d("draws", "textHeight:"+textHeight);
        //float lineHeight = fm.bottom - fm.top + fm.leading;

        if(curCustomWeekAdapter != null){
            curCustomWeekAdapter.setFlexibleConfig(pCurScrollLeftUp.fCol, pCurScrollLeftUp.fRow, pCurBlock.fCol, pCurBlock.fRow);
            curCustomWeekAdapter.updateCustomWeekItemList();
            //curCustomWeekAdapter.customWeekItemList
            if(curCustomWeekAdapter.listCalenderWeekItem != null){
                for(CalenderWeekItem item : curCustomWeekAdapter.listCalenderWeekItem){
                    RectF tempRectF = new RectF(item.left, item.top, item.right,item.bottom);
                    Log.d("draws", "draw rect / " + item.left + "/" + item.top + "/" + item.right + "/" +item.bottom);
                    //Log.d("draws", "customWeekItemList leng: "+ curCustomWeekAdapter.customWeekItemList.size());
                    tempRectPaint.setColor(item.getBlockColor());
                    tempTextPaint.setColor(item.getTextColor());

                    canvas.drawRoundRect(tempRectF, cornerRadius, cornerRadius, tempRectPaint);
                    if(item.isHistoryData()){
                        canvas.drawRoundRect(tempRectF, cornerRadius, cornerRadius, strokePaint);
                    }
                    else{
                        canvas.drawRoundRect(tempRectF, cornerRadius, cornerRadius, strokeTempPaint);
                    }

                    String tempString = item.getText();
                    int startIdx = 0, endIdx = tempString.length();
                    float curHeight = (float)0.0;
                    float totalHeight = tempRectF.height() - textHeight*4/3;
                    //Log.d("draws", "totalHeight:"+totalHeight);
                    //필요한 공간 > 실제 공간
                    if(curHeight + textHeight > totalHeight){   //첫 문장 들어갈 수 있는지 계산
                        continue;   //사각형크기 < 글씨 크기
                    }
                    //multiple line text
                    while(true){
                        int breakedCharLen = tempTextPaint.breakText(tempString.substring(startIdx), true, tempRectF.width() - 12, null);
                        if(breakedCharLen == 0){
                            break;  //더 이상 인쇄 할 문장이 없음.
                        }
                        //Log.d("draws", "breakedCharren:"+breakedCharLen);
                        //현재 문장 말고 다음 문장이 사각형을 벗어나는 경우
                        if(curHeight + textHeight + textHeight > totalHeight){
                            //필요한 공간 > 실제 공간
                            int summLen = min(breakedCharLen, 3);   //축약할 단어 길이를 정해진 문장 길이를 넘지 못하도록 함.
                            String finalString = tempString.substring(startIdx, startIdx + breakedCharLen - summLen); //축약
                            for(int loop = 0; loop < summLen; loop++){
                                finalString = finalString + ".";
                            }
                            canvas.drawText(finalString, 0, breakedCharLen, tempRectF.left + 6, tempRectF.top + curHeight + textHeight*2/3, tempTextPaint);
                            break;
                        }
                        else {
                            canvas.drawText(tempString, startIdx, startIdx + breakedCharLen, tempRectF.left + 6, tempRectF.top + curHeight + textHeight*2/3, tempTextPaint);
                            startIdx = startIdx + breakedCharLen;
                            curHeight += textHeight;
                        }
                    }
                }
            }
        }
        return;
    }

}