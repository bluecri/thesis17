package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sample.thesis17.mytimeapp.baseCalendar.month.MonthItem;

/**
 * Created by kimz on 2017-05-06.
 */
public class TimetableWeekItemView{
    //timetable item을 column에 따라서 저장(가변배열)
    private TimetableWeekItem[] timetableWeekItems;
    private int colNum;
    private int rowNum;
    private TimetableWeekItemBoxView[] onCanvasBoxviews;     //canvas 위에 그려질 item들.

    //기본 생성자들
    public TimetableWeekItemView(int colNum, int rowNum, long startDate, long endDate, float colBlockSize, float rowBlockSize){
        this.colNum = colNum;
        this.rowNum = rowNum;

        getDB();    //timetableWeekItem에 db 저장
        applyTempData();  //temp data가 존재하면 timetableWeekItem에 적용

        calcDrawingBoxViewCoord();  //현재 scroll좌표, block size를 이용하여 출력될 timetableWeekItems를 가려내고 출력할 box들의 좌표 계산 및 저장 -> boxView
    }
    //지정된 기간만큼의 DB를 가져옴. 양 끝단에 걸쳐있는 data도 포함.
    public void getDB(){


        //get db

        timetableWeekItems = new TimetableWeekItem[10/*db 개수*/];


        //if startDay <> endDay then push it each TimeTableWeekItem[a][], TimetableWeekItem[a+1][],,, a가 왼쪽 넘어간경우, a가 오른쪽 넘어간경우 제외
        //좌상단 우하단 결정.

		/*
		timetableWeekItem[0][0] = new TimetableWeekItem();
		....
		*/
    }

    //변경사항이 존재하면 timetableWeekItem에 변경사항 적용
    public void applyTempData(){

    }

    //box들의 좌표 계산 및 저장
    public void calcDrawingBoxViewCoord(){

    }

    public void clicked(int x, int y){
        //onCanvasBoxViews에서 해당 item Index 찾아내어...
    }
    /*
    public TimetableWeekItem[] getWeekItemsInCol(int colNum){
        //monthItem의 day를 가져와 text를 설정한다.
        //setText(String.valueOf(timetableItem.getiDayValue()));
    }
    public TimetableWeekItem getWeekItem(){
        return this.timetableWeekItem;
    }*/
}