package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.sample.thesis17.mytimeapp.DB.tables.ActiveListFixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;

import java.util.ArrayList;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by kimz on 2017-09-18.
 */

public class CustomWeekAdapter {
    List<FixedTimeTableData> arrLIstFixedTimeTableData = null;
    List<CustomWeekItem> customWeekItemList = null;

    //fixed
    private float scrollCol, scrollRow;   //최상단, 하단 위치
    private float colBlockSize, rowBlockSize;
    private long longStartDate = 0; //최초 시작 시간(Week의 start time)
    private Context curContext;

    //after calc
    private long one, two, three, four;


    //maybe fixed
    private float fUpSideSpace, fLeftSideSpace; //좌단, 상단 빈 공간 (요일 & 시간표시)
    private float fCustomViewWidthExceptSpace, fCustomViewHeightExceptSpace;    //좌단, 상단 빈 공간 제외(block이 그려질 공간 절대값)

    int countIdx = 0;   //block index count
    private float fCoordHeight, fCoordLeft, fCoordRight;


    CustomWeekAdapter(Context context, List<FixedTimeTableData> inListFixedTimeTableData){
        curContext = context;
        arrLIstFixedTimeTableData = inListFixedTimeTableData;
        customWeekItemList = new ArrayList<CustomWeekItem>();
    }

    public void setConfig(long longStartDate, float fUpSideSpace, float fLeftSideSpace, float fCustomViewWidthExceptSpace, float fCustomViewHeightExceptSpace){
        Log.d("draws", "sidespace:" + fUpSideSpace + "/"+  fLeftSideSpace + "/"+ fCustomViewWidthExceptSpace + "/"+ fCustomViewHeightExceptSpace);
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
        Log.d("draws", "updateCustomWeekItemList()");
        calcBorder();
        customWeekItemList.clear(); //새로 만듬.
        countIdx = 0;   //arrLIstFixedTimeTableData의 index

        //현재 arrLIstFixedTimeTableData를 순회하며 customWeekItemList를 만든다.
        for(FixedTimeTableData tempFTTData : arrLIstFixedTimeTableData){
            //if(isFixedTimeTableInTimeTable(tempFTTData)){
                //내부에 있는 경우에만 그림.
                long blockStartTime = tempFTTData.getlStartTime(), blockEndTime = tempFTTData.getlEndTime();    //left right
                long blockStartTimeModWithDay = blockStartTime % LONG_DAY_MILLIS, blockEndTimeModWithDay = blockEndTime % LONG_DAY_MILLIS;  //top, bottom
                //Log.d("draws", "three:"+three+"/four:"+four+"/one:"+one+"/two:"+two);
                //Log.d("draws", "blockStartTime : " + blockStartTime + "blockEndTime : " + blockEndTime + "blocktimemodday : " + blockStartTimeModWithDay + "blockendtimemodday" + blockEndTimeModWithDay);
                //starttime이나 endtime 둘중 하나가 범위내에 있는 경우, 해당 box가 window 내부에 존재함.
                /*if(((blockStartTime<=four&&three<=blockStartTime)||(blockEndTime<=four&&three<=blockEndTime))&&
                        ((blockStartTimeModWithDay<=two&&one<=blockStartTimeModWithDay)||(blockEndTimeModWithDay<=two&&one<=blockEndTimeModWithDay))){  시간으로 자르지 않고 사각형 좌표로 자르기로*/
                    //Log.d("draws", "updateCustomWeekItemList(), is in Window");
                    makeItemsWithFixedTimeTableData(tempFTTData);
                    /*CustomWeekItem tempCustomWeekItem = new CustomWeekItem();
                    tempCustomWeekItem.setText(tempFTTData.getStrFixedTimeTableName());
                    tempCustomWeekItem.setIdx(countIdx);

                    countIdx++;*/
                //}
            //}
            countIdx++;
        }
        Log.d("draws", "arrLIstFixedTimeTableData len : " + arrLIstFixedTimeTableData.size());
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
        four = ((long)Math.ceil((scrollCol+fCustomViewWidthExceptSpace)/colBlockSize))*LONG_DAY_MILLIS + longStartDate;
        Log.d("draws", "scrollCol:"+scrollCol + "excpwidth"+fCustomViewWidthExceptSpace + "colblocksize"+colBlockSize);
        //hour border
        one = ((long)Math.floor(scrollRow/rowBlockSize))*LONG_HOUR_MILLIS;
        two = ((long)Math.ceil((scrollRow+fCustomViewHeightExceptSpace)/rowBlockSize))*LONG_HOUR_MILLIS;
    }

    public void makeItemsWithFixedTimeTableData(FixedTimeTableData inData){
        Log.d("draws", "makeItemsWithFixedTimeTableData()");
        float left, up, right, bottom;
        //block이 중간에 잘리는지 확인한다.
        long startTime = inData.getlStartTime();
        long endTime = inData.getlEndTime();
        int timeModulo = 0;

        int startModulo = (int)(startTime % LONG_WEEK_MILLIS / LONG_DAY_MILLIS);
        int endModulo = (int)(endTime % LONG_WEEK_MILLIS / LONG_DAY_MILLIS);

        if(startTime > endTime){
            timeModulo = endModulo + 7 - startModulo;//(int)((endTime + LONG_WEEK_MILLIS - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }
        else{
            timeModulo = endModulo - startModulo;//(int)((endTime - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }


        CustomWeekItem tempCustomWeekItem = null;
        Log.d("draws", "timemodulo : " + timeModulo);

        switch(timeModulo){
            case 0:
                //same line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

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
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCustomWeekItem = new CustomWeekItem();
                tempCustomWeekItem.setIdx(countIdx);
                tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                tempCustomWeekItem.setCoords(left, up, right, bottom);

                customWeekItemList.add(tempCustomWeekItem);
                Log.d("draws", "block info : " + tempCustomWeekItem.toString());

                //multiple line
                for(int i=0; i<timeModulo-1; i++){
                    getCoordMindayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    left = fCoordLeft;
                    up = fCoordHeight;
                    getCoordMaxdayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    right = fCoordRight;
                    bottom = fCoordHeight;

                    tempCustomWeekItem = new CustomWeekItem();
                    tempCustomWeekItem.setIdx(countIdx);
                    tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                    tempCustomWeekItem.setCoords(left, up, right, bottom);

                    customWeekItemList.add(tempCustomWeekItem);
                    Log.d("draws", "block info : " + tempCustomWeekItem.toString());
                }

                getCoordMindayWithTimes(endTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCustomWeekItem = new CustomWeekItem();
                tempCustomWeekItem.setIdx(countIdx);
                tempCustomWeekItem.setText(inData.getStrFixedTimeTableName());
                tempCustomWeekItem.setCoords(left, up, right, bottom);

                customWeekItemList.add(tempCustomWeekItem);
                Log.d("draws", "block info : " + tempCustomWeekItem.toString());
                break;

        }
        Log.d("draws", "makeItemsWithFixedTimeTableData() end");
    }

    public void getCoordWithTimes(long times){
        if(times < longStartDate){
            times = longStartDate;  //week time 0 이전일 경우 longStartDate로 계산한다.
        }
        if(longStartDate + LONG_WEEK_MILLIS < times){
            times = longStartDate + LONG_WEEK_MILLIS - 1;  //week time이 week 이후인 경우 longStartDatef로 계산한다.
        }
        //hour를 0~24로 표현한 뒤 rowBlockSIze(1hour)를 곱한다. 그 뒤 scroll위치와 sideSpace위치를 고려한 좌표를 반환한다.
        fCoordHeight = min(max((((float)(times%LONG_DAY_MILLIS))/LONG_HOUR_MILLIS)*rowBlockSize-scrollRow+fUpSideSpace, fUpSideSpace), fUpSideSpace+fCustomViewHeightExceptSpace);
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.

        fCoordLeft = max(((times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS*colBlockSize-scrollCol+fLeftSideSpace, fLeftSideSpace);

        if(fCoordLeft == fLeftSideSpace){
            fCoordRight = fCoordLeft + colBlockSize + ((times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS*colBlockSize-scrollCol;
        }
        else{
            fCoordRight = fCoordLeft + colBlockSize;
        }
        return;
    }

    //해당 날짜의 0시 0분에 가장 가까우면서 window에 valid한 좌표
    public void getCoordMindayWithTimes(long times){
        //가장 상위 좌표
        //fCoordHeight = fUpSideSpace-scrollRow;
        fCoordHeight = fUpSideSpace;
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.
        //fCoordLeft = (((float)(times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS)*colBlockSize-scrollCol+fLeftSideSpace;
        fCoordLeft = max(((times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS*colBlockSize-scrollCol+fLeftSideSpace, fLeftSideSpace);

        return;
    }

    //해당 날짜의 23시 59분 59초에 가장 가까우면서 window에 valid한 좌표
    public void getCoordMaxdayWithTimes(long times){
        //가장 하위 좌표
        //fCoordHeight = rowBlockSize*24+fUpSideSpace-scrollRow;
        fCoordHeight = min(rowBlockSize*24+fUpSideSpace, fUpSideSpace + fCustomViewHeightExceptSpace);
        //day를 0~7으로 표현한 뒤 colBlockSize만큼 곱한다. 그 뒤 scroll 위치와 sideSpace 위치를 고려한 좌표를 반환한다.
        //fCoordLeft = (((float)(times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS)*colBlockSize-scrollCol+fLeftSideSpace;
        fCoordLeft = max(((times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS*colBlockSize-scrollCol+fLeftSideSpace, fLeftSideSpace);

        if(fCoordLeft == fLeftSideSpace){
            fCoordRight = fCoordLeft + colBlockSize + ((times%LONG_WEEK_MILLIS))/LONG_DAY_MILLIS*colBlockSize-scrollCol;
        }
        else{
            fCoordRight = fCoordLeft + colBlockSize;
        }
        return;
    }

    public int getIdxWithClicked(float x, float y){
        for(CustomWeekItem item : customWeekItemList){
            if(item.getLeft() <= x && x < item.getRight() && item.getTop() <= y && y < item.getBottom()){
                ((TimetableWeekFragment)(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("timetable_week_fragment"))).openDialogWithIdx(item.getIdx());
                return item.getIdx();
            }
        }
        Log.d("CustomWeekAdapter", "getIdxWithClicked return -1");
        return -1;
    }
}
