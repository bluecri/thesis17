package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kimz on 2017-05-07.
 */

public class CustomWeekView extends View {
    private Paint paint;
    private int canvasColNum, canvasRowNum;  //number of canvas Col, Row
    private int subRowNum; //number of subRow in 1 Row
    private float fMaxBlockCol, fMinBlockCol, fMaxBlockRow, fMinBlockRow; //min, max size of each block

    private int customViewWidth, customViewHeight;  //px size of CustomView


    private Point pCurScrollLeftUp;   //스크롤시 좌상단 좌표
    //private Point pCurScrollSize;   //보이는 스크롤의 사이즈

    private Point pCurBlock;   //current size of block

    public void init(){
        setCustomViewWidthHeight(); //setting CustomView size
        setCanvasBlock(7, 24, 2);   //setting week block

        //초기 블럭의 size setting(min)
        pCurBlock.setFPointCol(fMinBlockCol);
        pCurBlock.setFPointRow(fMinBlockRow);

        //초기 스크롤 setting
        pCurScrollLeftUp.setFPointColRow(0, 0);
    }

    //현재 customView의 높이 너비 크기를 px size로 받는다.
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
        fMaxBlockRow = (float) customViewHeight / 3;
        fMaxBlockCol = (float) customViewWidth / 3;
        //블럭의 최소 사이즈
        fMinBlockRow = (float) customViewHeight / 15;
        fMinBlockCol = (float) customViewWidth / 7;
    }



    public void drawAllBaseBlock(Canvas canvas){
        int iRowStartIdx = (int)Math.floor(pCurScrollLeftUp.fRow/pCurBlock.fRow);       //row의 시작 index.
        float fRowposition = (iRowStartIdx + 1) * pCurBlock.fRow - pCurScrollLeftUp.fRow;
        float fBoldLine = 0.5f;     //굵은 라인
        float fThinLine = 0.2f;     //얇은 라인
        float fAcc = pCurBlock.fRow/subRowNum;  //subline만큼씩 위치 증가
        paint.setColor(Color.BLACK);    //line color black

        int iRowEndIdx = (int)Math.floor((pCurScrollLeftUp.fRow + (float)customViewHeight)/pCurBlock.fRow);     //row 마지막 줄 index. (스크롤높이 + view 높이)/블록높이
        for(int rowLines = iRowStartIdx; rowLines <= iRowEndIdx; rowLines++){    //draw main lines
            //draw Main line in fRowposition
            paint.setStrokeWidth(fBoldLine);
            canvas.drawLine
            fRowposition += (fAcc);
            for(int subRowLines = 0; subRowLines < canvasRowNum-1; subRowLines++){ //draw sublines
                //draw Subline in fRowPosition
                paint.setStrokeWidth(fThinLine);

                fRowposition += (fAcc);
            }
        }
    }








    public CustomWeekView(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public CustomWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.RED);
    }


    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawRect(0,0,200,200, paint);

    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Toast.makeText(super.getContext(), "pos : " + event.getX() + ", " + event.getY() + ", " + getWidth() + ", " + getHeight(), Toast.LENGTH_LONG).show();
        }
        return super.onTouchEvent(event);
    }

    //Customview의 size에 따라 block의 최대, 최소 크기 조정

}
