package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.util.Log;

import com.sample.thesis17.mytimeapp.DB.tables.ActiveListFixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;

import java.util.ArrayList;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;

/**
 * Created by kimz on 2017-09-18.
 */

public class CustomWeekAdapter {
    List<FixedTimeTableData> arrLIstFixedTimeTableData = null;
    List<CustomWeekItem> customWeekItemList = null;

    //fixed
    float scrollCol, scrollRow;   //최상단, 하단 위치
    float colBlockSize, rowBlockSize;
    long longStartDate = 0; //최초 시작 시간(Week의 start time)

    //after calc
    long one, two, three, four;


    //maybe fixed
    float fUpSideSpace, fLeftSideSpace; //좌단, 상단 빈 공간 (요일 & 시간표시)
    float fCustomViewWidthExceptSpace, fCustomViewHeightExceptSpace;    //좌단, 상단 빈 공간 제외(block이 그려질 공간 절대값)

    int countIdx = 0;   //block index count
    float fCoordHeight, fCoordLeft;


    CustomWeekAdapter(List<FixedTimeTableData> inListFixedTimeTableData){
        arrLIstFixedTimeTableData = inListFixedTimeTableData;
    }

    public void setConfig(long longStartDate, float fUpSideSpace, float fLeftSideSpace, float fCustomViewWidthExceptSpace, float fCustomViewHeightExceptSpace){
        this.fLeftSideSpace = fLeftSideSpace;
        this.fUpSideSpace = fUpSideSpace;
        this.fCustomViewWidthExceptSpace = fCustomViewWidthExceptSpace;
        this.fCustomViewHeightExceptSpace = fCustomViewHeightExceptSpace;
        this.longStartDate = longStartDate;
    }

    public void setFlexibleConfig(float scrollCol, float scrollRow, float colBlockSize, float rowBlockSize){
        this.scrollCol = scrollCol;
        this.scrollRow = scrollRow;
        this.colBlockSize = colBlockSize;
        this.rowBlockSize = rowBlockSize;
        //this.longStartDate = longStartDate;
    }

    //arrLIstFixedTimeTableData를 돌며 block을 계산(customWeekItemList)한다.
    public void updateCustomWeekItemList(){
        calcBorder();
        customWeekItemList.clear(); //새로 만듬.
        countIdx = 0;   //arrLIstFixedTimeTableData의 index

        //현재 arrLIstFixedTimeTableData를 순회하며 customWeekItemList를 만든다.
        for(FixedTimeTableData tempFTTData : arrLIstFixedTimeTableData){
            //if(isFixedTimeTableInTimeTable(tempFTTData)){
                //내부에 있는 경우에만 그림.
                long blockStartTime = tempFTTData.getlStartTime(), blockEndTime = tempFTTData.getlEndTime();
                long blockStartTimeModWithDay = blockStartTime % LONG_DAY_MILLIS, blockEndTimeModWithDay = blockEndTime % LONG_DAY_MILLIS;

                //starttime이나 endtime 둘중 하나가 범위내에 있는 경우, 해당 box가 window 내부에 존재함.
                if(((blockStartTime<=four&&three<=blockStartTime)||(blockEndTime<=four&&three<=blockEndTime))&&
                ((blockStartTimeModWithDay<=two&&one<=blockStartTimeModWithDay)||(blockStartTimeModWithDay<=two&&one<=blockStartTimeModWithDay))){
                    makeItemsWithFixedTimeTableData(tempFTTData);
                    /*CustomWeekItem tempCustomWeekItem = new CustomWeekItem();
                    tempCustomWeekItem.setText(tempFTTData.getStrFixedTimeTableName());
                    tempCustomWeekItem.setIdx(countIdx);

                    countIdx++;*/
                }
            //}
            countIdx++;
        }
    }

    //해당 FixedTimeTableData가 현재 timetable 내부에 있는지
    public boolean isFixedTimeTableInTimeTable(FixedTimeTableData data){
        return false;
    }

    //이미 생성되어 있는 view item list 반환
    public List<CustomWeekItem> getItemList(){
        if(customWeekItemList == null){
            return new ArrayList<CustomWeekItem>();
        }
        return customWeekItemList;
    }

    public void calcBorder(){
        //day border
        three = ((long)Math.floor(scrollCol/colBlockSize))*LONG_DAY_MILLIS + longStartDate;
        four = ((long)Math.ceil(scrollCol+fCustomViewWidthExceptSpace/colBlockSize))*LONG_DAY_MILLIS + longStartDate;
        //hour border
        one = ((long)Math.floor(scrollRow/rowBlockSize))*LONG_HOUR_MILLIS;
        two = ((long)Math.ceil(scrollRow+fCustomViewHeightExceptSpace/rowBlockSize))*LONG_HOUR_MILLIS;
    }

    public void makeItemsWithFixedTimeTableData(FixedTimeTableData inData){
        float left, up, right, bottom;
        //block이 중간에 잘리는지 확인한다.
        long startTime = inData.getlStartTime();
        long endTime = inData.getlEndTime();
        int timeModulo = (int)((endTime - startTime) % LONG_WEEK_MILLIS);

        CustomWeekItem tempCustomWeekItem = null;

        switch(timeModulo){
            case 0:
                //same line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordLeft + colBlockSize;
                bottom = fCoordHeight + rowBlockSize;

                tempCustomWeekItem = new CustomWeekItem();
                tempCustomWeekItem.setIdx(countIdx);
                tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                tempCustomWeekItem.setCoords(left, up, right, bottom);

                customWeekItemList.add(tempCustomWeekItem);
                break;
            default:
                //different line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordMaxdayWithTimes(startTime);
                right = fCoordLeft + colBlockSize;
                bottom = fCoordHeight + rowBlockSize;

                tempCustomWeekItem = new CustomWeekItem();
                tempCustomWeekItem.setIdx(countIdx);
                tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                tempCustomWeekItem.setCoords(left, up, right, bottom);

                customWeekItemList.add(tempCustomWeekItem);

                //multiple line
                for(int i=0; i<timeModulo-1; i++){
                    getCoordWithTimes(startTime+i*LONG_DAY_MILLIS);
                    left = fCoordLeft;
                    up = fCoordHeight;
                    getCoordMaxdayWithTimes(startTime+i*LONG_DAY_MILLIS);
                    right = fCoordLeft + colBlockSize;
                    bottom = fCoordHeight + rowBlockSize;

                    tempCustomWeekItem = new CustomWeekItem();
                    tempCustomWeekItem.setIdx(countIdx);
                    tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                    tempCustomWeekItem.setCoords(left, up, right, bottom);

                    customWeekItemList.add(tempCustomWeekItem);
                }

                getCoordMindayWithTimes(endTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordLeft + colBlockSize;
                bottom = fCoordHeight + rowBlockSize;

                tempCustomWeekItem = new CustomWeekItem();
                tempCustomWeekItem.setIdx(countIdx);
                tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                tempCustomWeekItem.setCoords(left, up, right, bottom);

                customWeekItemList.add(tempCustomWeekItem);
                break;

        }
    }

    public void getCoordWithTimes(long times){
        if(times < longStartDate){
            times = longStartDate;  //week time 0 이전일 경우 longStartDate로 계산한다.
        }
        if(longStartDate < times){
            times = longStartDate + LONG_DAY_MILLIS*7;  //week time이 week 이후인 경우 longStartDatef로 계산한다.
        }
        //hour를 0~24로 표현한 뒤 rowBlockSIze(1hour)를 곱한다. 그 뒤 scroll위치와 sideSpace위치를 고려한 좌표를 반환한다.
        fCoordHeight = (((float)(times%LONG_DAY_MILLIS))/LONG_HOUR_MILLIS)*rowBlockSize-scrollRow+fUpSideSpace;
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.
        fCoordLeft = (((float)(times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS)*colBlockSize-scrollCol+fLeftSideSpace;
        return;
    }

    //해당 날짜의 0시 0분에 가장 가까우면서 window에 valid한 좌표
    public void getCoordMindayWithTimes(long times){
        //가장 상위 좌표
        fCoordHeight = fUpSideSpace-scrollRow;
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.
        fCoordLeft = (((float)(times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS)*colBlockSize-scrollCol+fLeftSideSpace;
        return;
    }

    //해당 날짜의 23시 59분 59초에 가장 가까우면서 window에 valid한 좌표
    public void getCoordMaxdayWithTimes(long times){
        //가장 하위 좌표
        fCoordHeight = rowBlockSize*24+fUpSideSpace-scrollRow;
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.
        fCoordLeft = (((float)(times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS)*colBlockSize-scrollCol+fLeftSideSpace;
        return;
    }

    public int getIdxWithClicked(float x, float y){
        for(CustomWeekItem item : customWeekItemList){
            if(item.getLeft() <= x && x < item.getRight() && item.getTop() <= y && item.getBottom() < y){
                return item.getIdx();
            }
        }
        Log.d("CustomWeekAdapter", "getIdxWithClicked return -1");
        return -1;
    }
}
