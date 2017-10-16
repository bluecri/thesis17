package com.sample.thesis17.mytimeapp.baseCalendar.week;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData;
import com.sample.thesis17.mytimeapp.baseCalendar.CalenderWeekItemIdxWithIsHistoryData;

import java.util.ArrayList;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by kimz on 2017-09-21.
 */

public class CalenderWeekAdapter {
    List<HistoryData> listHistoryData = null;
    List<TempHistoryData> listTempHistoryData = null;
    List<CalenderWeekItem> listCalenderWeekItem = null;

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

    private int countIdx = 0;   //block index count
    private float fCoordHeight, fCoordLeft, fCoordRight;


    CalenderWeekAdapter(List<HistoryData> inlistHistoryData, List<TempHistoryData> inlistTempHistoryData){
        listHistoryData = inlistHistoryData;
        listTempHistoryData = inlistTempHistoryData;
        listCalenderWeekItem = new ArrayList<CalenderWeekItem>();
    }
    public void setLongStartDate(long in){
        this.longStartDate = in;
    }

    public void setConfig(float fUpSideSpace, float fLeftSideSpace, float fCustomViewWidthExceptSpace, float fCustomViewHeightExceptSpace){
        Log.d("draws", "sidespace:" + fUpSideSpace + "/"+  fLeftSideSpace + "/"+ fCustomViewWidthExceptSpace + "/"+ fCustomViewHeightExceptSpace);
        this.fLeftSideSpace = fLeftSideSpace;
        this.fUpSideSpace = fUpSideSpace;
        this.fCustomViewWidthExceptSpace = fCustomViewWidthExceptSpace;
        this.fCustomViewHeightExceptSpace = fCustomViewHeightExceptSpace;
        //this.longStartDate = longStartDate;
    }

    public void setFlexibleConfig(float scrollCol, float scrollRow, float colBlockSize, float rowBlockSize){
        this.scrollCol = scrollCol;
        this.scrollRow = scrollRow;
        this.colBlockSize = colBlockSize;
        this.rowBlockSize = rowBlockSize;
        //this.longStartDate = longStartDate;
    }

    //arrLIstFixedTimeTableData를 돌며 block을 계산(listCalenderWeekItem)한다.
    public void updateCustomWeekItemList(){
        Log.d("draws", "updateCustomWeekItemList()");
        calcBorder();
        listCalenderWeekItem.clear(); //새로 만듬.
        countIdx = 0;   //listHistoryData, listTempHistoryData index

        //현재 listHistoryData 순회하며 listCalenderWeekItem 만든다.
        for(HistoryData hd : listHistoryData){
            //내부에 있는 경우에만 그림.
            long blockStartTime = hd.getlStartTime(), blockEndTime = hd.getlEndTime();
            long blockStartTimeModWithDay = blockStartTime % LONG_DAY_MILLIS, blockEndTimeModWithDay = blockEndTime % LONG_DAY_MILLIS;
            //starttime이나 endtime 둘중 하나가 범위내에 있는 경우, 해당 box가 window 내부에 존재함.
            /*if(((blockStartTime<=four&&three<=blockStartTime)||(blockEndTime<=four&&three<=blockEndTime))&&
                    ((blockStartTimeModWithDay<=two&&one<=blockStartTimeModWithDay)||(blockEndTimeModWithDay<=two&&one<=blockEndTimeModWithDay))){*/
                makeItemsWithHistoryData(hd);

            //}
            //}
            countIdx++;
        }
        Log.d("draws", "listHistoryData len : " + listHistoryData.size());

        countIdx = 0;   //init idx
        //현재 listTempHistoryData 순회하며 listCalenderWeekItem 만든다.
        for(TempHistoryData thd : listTempHistoryData){
            //내부에 있는 경우에만 그림.
            long blockStartTime = thd.getlStartTime(), blockEndTime = thd.getlEndTime();
            long blockStartTimeModWithDay = blockStartTime % LONG_DAY_MILLIS, blockEndTimeModWithDay = blockEndTime % LONG_DAY_MILLIS;
            //starttime이나 endtime 둘중 하나가 범위내에 있는 경우, 해당 box가 window 내부에 존재함.
            /*if(((blockStartTime<=four&&three<=blockStartTime)||(blockEndTime<=four&&three<=blockEndTime))&&
                    ((blockStartTimeModWithDay<=two&&one<=blockStartTimeModWithDay)||(blockEndTimeModWithDay<=two&&one<=blockEndTimeModWithDay))){*/
                makeItemsWithTempHistoryData(thd);

            //}
            //}
            countIdx++;
        }
        Log.d("draws", "listTempHistoryData len : " + listTempHistoryData.size());
    }


    //이미 생성되어 있는 view item list 반환
    public List<CalenderWeekItem> listCalenderWeekItem(){
        if(listCalenderWeekItem == null){
            return new ArrayList<CalenderWeekItem>();
        }
        return listCalenderWeekItem;
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

    public void makeItemsWithHistoryData(HistoryData inData){
        Log.d("draws", "makeItemsWithHistoryData()");
        float left, up, right, bottom;
        //block이 중간에 잘리는지 확인한다.
        long startTime = inData.getlStartTime();
        long endTime = inData.getlEndTime();
        int timeModulo = 0;

        if(startTime > endTime){
            timeModulo = (int)((endTime + LONG_WEEK_MILLIS - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }
        else{
            timeModulo = (int)((endTime - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }

        CalenderWeekItem tempCalenderWeekItem = null;

        switch(timeModulo){
            case 0:
                //same line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(true);

                listCalenderWeekItem.add(tempCalenderWeekItem);
                break;
            default:
                //different line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordMaxdayWithTimes(startTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(true);

                listCalenderWeekItem.add(tempCalenderWeekItem);

                //multiple line
                for(int i=0; i<timeModulo-1; i++){
                    getCoordMindayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    left = fCoordLeft;
                    up = fCoordHeight;
                    getCoordMaxdayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    right = fCoordRight;
                    bottom = fCoordHeight;

                    tempCalenderWeekItem = new CalenderWeekItem();
                    tempCalenderWeekItem.setIdx(countIdx);
                    tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                    tempCalenderWeekItem.setCoords(left, up, right, bottom);
                    tempCalenderWeekItem.setHistoryData(true);

                    listCalenderWeekItem.add(tempCalenderWeekItem);
                }

                getCoordMindayWithTimes(endTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(true);

                listCalenderWeekItem.add(tempCalenderWeekItem);
                break;

        }
        Log.d("draws", "makeItemsWithFixedTimeTableData() end");
    }

    public void makeItemsWithTempHistoryData(TempHistoryData inData){
        Log.d("draws", "makeItemsWithHistoryData()");
        float left, up, right, bottom;
        //block이 중간에 잘리는지 확인한다.
        long startTime = inData.getlStartTime();
        long endTime = inData.getlEndTime();

        int timeModulo = 0;

        if(startTime > endTime){
            timeModulo = (int)((endTime + LONG_WEEK_MILLIS - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }
        else{
            timeModulo = (int)((endTime - startTime) % LONG_WEEK_MILLIS/LONG_DAY_MILLIS);
        }

        CalenderWeekItem tempCalenderWeekItem = null;

        switch(timeModulo){
            case 0:
                //same line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(false);

                listCalenderWeekItem.add(tempCalenderWeekItem);
                break;
            default:
                //different line
                getCoordWithTimes(startTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordMaxdayWithTimes(startTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(false);

                listCalenderWeekItem.add(tempCalenderWeekItem);

                //multiple line
                for(int i=0; i<timeModulo-1; i++){
                    getCoordMindayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    left = fCoordLeft;
                    up = fCoordHeight;
                    getCoordMaxdayWithTimes(startTime+(i+1)*LONG_DAY_MILLIS % LONG_WEEK_MILLIS);
                    right = fCoordRight;
                    bottom = fCoordHeight;

                    tempCalenderWeekItem = new CalenderWeekItem();
                    tempCalenderWeekItem.setIdx(countIdx);
                    tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                    tempCalenderWeekItem.setCoords(left, up, right, bottom);
                    tempCalenderWeekItem.setHistoryData(false);

                    listCalenderWeekItem.add(tempCalenderWeekItem);
                }

                getCoordMindayWithTimes(endTime);
                left = fCoordLeft;
                up = fCoordHeight;
                getCoordWithTimes(endTime);
                right = fCoordRight;
                bottom = fCoordHeight;

                tempCalenderWeekItem = new CalenderWeekItem();
                tempCalenderWeekItem.setIdx(countIdx);
                tempCalenderWeekItem.setText(inData.getForeFixedTimeTable().getStrFixedTimeTableName());
                tempCalenderWeekItem.setCoords(left, up, right, bottom);
                tempCalenderWeekItem.setHistoryData(false);

                listCalenderWeekItem.add(tempCalenderWeekItem);
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
        for(CalenderWeekItem item : listCalenderWeekItem){
            if(item.getLeft() <= x && x < item.getRight() && item.getTop() <= y && y < item.getBottom()){
                CalenderWeekItemIdxWithIsHistoryData retData = new CalenderWeekItemIdxWithIsHistoryData(item.getIdx(), item.isHistoryData());
                ((CalenderWeekFragment)(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("calender_week_fragment"))).openDialogWithIdx(retData);
                return item.getIdx();
            }
        }
        Log.d("CalenderWeekAdapter", "getIdxWithClicked return -1");
        return -1;
    }
}
