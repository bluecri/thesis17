package com.sample.thesis17.mytimeapp.baseTimeTable.day;

/**
 * Created by kimz on 2017-10-31.
 */

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.baseTimeTable.CustomWeekItem;

import java.util.ArrayList;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_MIN_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.getContrastColor;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by kimz on 2017-09-18.
 */

public class CustomDayAdapter {
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


    CustomDayAdapter(Context context, List<FixedTimeTableData> inListFixedTimeTableData){
        curContext = context;
        arrLIstFixedTimeTableData = inListFixedTimeTableData;
        customWeekItemList = new ArrayList<CustomWeekItem>();
    }
    public void setLongStartDate(long lStartTime){
        longStartDate = lStartTime;
    }

    public void setConfig(long longStartDate, float fUpSideSpace, float fLeftSideSpace, float fCustomViewWidthExceptSpace, float fCustomViewHeightExceptSpace){
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

    //arrLIstFixedTimeTableData를 돌며 block을 계산(customWeekItemList)한다.
    public void updateCustomWeekItemList(){
        Log.d("draws", "updateCustomWeekItemList()");
        calcBorder();
        customWeekItemList.clear(); //새로 만듬.
        if(listListCustomWeekItem != null){
            for(List<CustomWeekItem> listItem: listListCustomWeekItem){
                listItem.clear();
            }
        }
        listListCustomWeekItem = new ArrayList<List<CustomWeekItem>>();
        listListCustomWeekItem.add(new ArrayList<CustomWeekItem>());
        countIdx = 0;   //arrLIstFixedTimeTableData의 index

        //현재 arrLIstFixedTimeTableData를 순회하며 customWeekItemList를 만든다.
        for(FixedTimeTableData tempFTTData : arrLIstFixedTimeTableData){
            //내부에 있는 경우에만 그림.
            long blockStartTime = tempFTTData.getlStartTime(), blockEndTime = tempFTTData.getlEndTime();    //left right
            long blockStartTimeModWithDay = blockStartTime % LONG_DAY_MILLIS, blockEndTimeModWithDay = blockEndTime % LONG_DAY_MILLIS;  //top, bottom
            makeItemsWithFixedTimeTableData(tempFTTData);
        }
        Log.d("draws", "arrLIstFixedTimeTableData len : " + arrLIstFixedTimeTableData.size());

        for(List<CustomWeekItem> listItem : listListCustomWeekItem){
            customWeekItemList.addAll(listItem);
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
        four = ((long)Math.ceil((scrollCol+fCustomViewWidthExceptSpace)/colBlockSize))*LONG_DAY_MILLIS + longStartDate;
        Log.d("draws", "scrollCol:"+scrollCol + "excpwidth"+fCustomViewWidthExceptSpace + "colblocksize"+colBlockSize);
        //hour border
        one = ((long)Math.floor(scrollRow/rowBlockSize))*LONG_HOUR_MILLIS;
        two = ((long)Math.ceil((scrollRow+fCustomViewHeightExceptSpace)/rowBlockSize))*LONG_HOUR_MILLIS;
    }

    public List<List<CustomWeekItem>> listListCustomWeekItem;


    public void makeItemsWithFixedTimeTableData(FixedTimeTableData inData){
        Log.d("draws", "makeItemsWithFixedTimeTableData()");
        float left, up, right, bottom;
        //block이 중간에 잘리는지 확인한다.
        long startTime = inData.getlStartTime();
        long endTime = inData.getlEndTime();
        int timeModulo = 0;


        CustomWeekItem tempCustomWeekItem = null;
        tempCustomWeekItem = new CustomWeekItem();
        up = fCoordHeight;
        bottom = fCoordHeight;

        boolean canIn = true;
        int targetColIdx = 0;
        for(int i = 0, ii = listListCustomWeekItem.size(); i<ii; i++){
            List<CustomWeekItem> listItem = listListCustomWeekItem.get(i);
            canIn = true;
            for(CustomWeekItem item : listItem){
                if(item.getStartTime() < endTime && startTime < item.getEndTime()){ //겹침
                    canIn = false;
                    break;
                }
            }
            if(canIn == true){
                //make block with column i
                targetColIdx = i;
                break;
            }
            else{
                //re calc with next column
            }
        }
        if(canIn == false){
            //make new list with listListCustomWeekItem.size() column
            targetColIdx = listListCustomWeekItem.size();
            listListCustomWeekItem.add(new ArrayList<CustomWeekItem>());

        }

        //etCoordWithTimes(startTime);

        up = min(max((((float)(maxL(startTime, longStartDate)%LONG_DAY_MILLIS))/LONG_HOUR_MILLIS)*rowBlockSize-scrollRow+fUpSideSpace, fUpSideSpace), fUpSideSpace+fCustomViewHeightExceptSpace);
        bottom = min(max((((float)(minL(endTime, longStartDate + LONG_DAY_MILLIS - LONG_MIN_MILLIS/2)%LONG_DAY_MILLIS))/LONG_HOUR_MILLIS)*rowBlockSize-scrollRow+fUpSideSpace, fUpSideSpace), fUpSideSpace+fCustomViewHeightExceptSpace);

        Log.d("indexOfBottom","indexOfBottom" + bottom + "/longStartDate : " + longStartDate + "/endTime" + endTime);
        left = max((targetColIdx)*colBlockSize -scrollCol+fLeftSideSpace, fLeftSideSpace);

        if(left == fLeftSideSpace){
            right = left + colBlockSize + targetColIdx*colBlockSize-scrollCol;
        }
        else{
            right = left + colBlockSize;
        }

        tempCustomWeekItem.setIdx(countIdx);
        tempCustomWeekItem.setStartTime(startTime);
        tempCustomWeekItem.setEndTime(endTime);
        tempCustomWeekItem.setText(inData.getStrFixedTimeTableName() + " / " + inData.getStrMemo());
        tempCustomWeekItem.setCoords(left, up, right, bottom);
        tempCustomWeekItem.setBlockColor(inData.getColor());
        tempCustomWeekItem.setTextColor(getContrastColor(inData.getColor()));

        listListCustomWeekItem.get(targetColIdx).add(tempCustomWeekItem);
        countIdx++;
    }


    public int getIdxWithClicked(float x, float y){
        for(CustomWeekItem item : customWeekItemList){
            if(item.getLeft() <= x && x < item.getRight() && item.getTop() <= y && y < item.getBottom()){
                ((TimetableDayFragment)(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("timetable_day_fragment"))).openDialogWithIdx(item.getIdx());
                return item.getIdx();
            }
        }
        Log.d("CustomWeekAdapter", "getIdxWithClicked return -1");
        return -1;
    }

    long minL(long d1, long d2){
        if(d1 < d2){
            return d1;
        }
        return d2;
    }

    long maxL(long d1, long d2){
        if(d1 > d2){
            return d1;
        }
        return d2;
    }
}
