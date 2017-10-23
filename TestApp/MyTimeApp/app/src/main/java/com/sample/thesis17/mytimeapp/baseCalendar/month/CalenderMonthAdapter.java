package com.sample.thesis17.mytimeapp.baseCalendar.month;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.Calendar;

/**
 * Created by kimz on 2017-05-01.
 */

public class CalenderMonthAdapter extends BaseAdapter{
    public static final String TAG = "MonthAdapter";    //for debug

    Context mContext;   //어댑터를 생성하는 context

    private MonthItem[] monthItems;
    Calendar calender;  //날짜 계산위한 java calendar
    private int numDayofWeek = 7;   //week = 7day
    int firstDay;   //달력 시작 날짜
    int lastDay;

    int currentYear;
    int currentMonth;

    int mStartDay, startDay;    //달력의 시작 일수(1일부터 시작) , 나라별 시작 요일

    long inParamTimeLong = 0;

    private int selectedPosition = -1;  //초기 selected position


    public CalenderMonthAdapter(Context context, long inTime){
        super();
        mContext = context;
        inParamTimeLong = inTime;
        init();
    }

    public CalenderMonthAdapter(Context context, AttributeSet attr){
        super();
        mContext = context;
        init();
    }
    private void init() {
        monthItems = new MonthItem[8 * 6];

        calender = Calendar.getInstance();      //calendar 시작지점.
        calender.setTimeInMillis(inParamTimeLong);
        recalculate();
        resetDayNumbers();
    }

    public long setPreviousMonth() {
        calender.add(Calendar.MONTH, -1);       //calendar의 month 계산
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;      //position 초기화
        inParamTimeLong = calender.getTimeInMillis();
        return calender.getTimeInMillis();
    }

    public long setNextMonth() {
        calender.add(Calendar.MONTH, 1);
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;
        inParamTimeLong = calender.getTimeInMillis();
        return calender.getTimeInMillis();
    }

    //selectedPosition Get/Set function
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
    public int getSelectedPosition(){
        return this.selectedPosition;
    }

    //java calendar를 이용한 날짜 계산 function
    public void recalculate(){
        //calendar의 월별 첫번째 day의 수(1일이 시작지점)
        calender.set(Calendar.DAY_OF_MONTH, 1); //mStartDay set

        // get week day
        int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
        firstDay = getFirstDay(dayOfWeek);  //달력 시작지점(월요일)을 0으로 볼 때 1일이 시작되는 index

        mStartDay = calender.getFirstDayOfWeek();       // -- 1
        currentYear = calender.get(Calendar.YEAR);
        currentMonth = calender.get(Calendar.MONTH);
        lastDay = getMonthLastDay(currentYear, currentMonth);   //월별 마지막 일

        //Log.d(TAG, "curYear : " + currentYear + ", curMonth : " + currentMonth + ", lastDay : " + lastDay);

        int diff = mStartDay - Calendar.SUNDAY - 1;
        startDay = getFirstDayOfWeek();     //나라별 달력의 시작 week(일/토/월)가 다름.
        Log.d(TAG, "dayofweek" + dayOfWeek + " firstday : " + firstDay+ " mStartDay : " + mStartDay + ", startDay : " + startDay + "lastDay : " + lastDay);
    }

    //month item의 모든 day 재변경
    public void resetDayNumbers() {
        //for (int i = 0; i < 42; i++) {
        int iday = 0;
        //int weekNumAcc = 0;   //Week Number를 구하기 위한 ACC

        for (int i = 0; i < 48; i++) {
                // calculate day number
              if(i%8 != 0){
                  int dayNumber = (iday+1) - firstDay;      //실제 Index - 첫 1일 시작 index + 1
                  if (dayNumber < 1 || dayNumber > lastDay) {   //일수가 1~ 월별 마지막 일 사이인 경우.
                      dayNumber = 0;
                  }
                  ++iday;

                  // save as a data item
                  monthItems[i] = new MonthItem(dayNumber, false);
              }
              else{ //week
                  //int weekNum = calender.get(Calendar.WEEK_OF_YEAR);
                  int dayNumber = (iday+1) - firstDay;
                  if (iday != 0 && (dayNumber < 1 || dayNumber > lastDay)){     //week is first row of month && valid dayNumber
                      monthItems[i] = new MonthItem(0, true);
                  }
                  else{
                      //int weekNum = calender.get(Calendar.WEEK_OF_YEAR);
                      Long getMillis = 0L;


                      Calendar tempCal = Calendar.getInstance();
                      tempCal.set(currentYear, currentMonth, 1, 0, 0, 0);
                      tempCal.add(Calendar.DAY_OF_MONTH, dayNumber-1);
                      getMillis = tempCal.getTimeInMillis();
                      Log.d("calendCalc", "getCurWeekWithYewrMonthDay " + tempCal.getTime() + tempCal.getTimeInMillis());

                      //getCurMillisWithYearAndWeek(year, tempCal.get(Calendar.WEEK_OF_YEAR));

                      int weekNum = tempCal.get(Calendar.WEEK_OF_YEAR);     //getCurWeekWithYewrMonthDay(currentYear, currentMonth, dayNumber, getMillis);       //1'st week of year.month + week acc

                      Log.d("calendCalc", "week : " + weekNum + " / Y:M:D : " + currentYear + ":" + (currentMonth+1) + ":" + dayNumber);
                      Log.d("calendCalc", "inLong : " + getMillis);
                      monthItems[i] = new MonthItem(weekNum, true);
                      monthItems[i].setlWeekValue(getMillis);

                      //monthItems[i] = new MonthItem(weekNum+weekNumAcc, true);
                      //++weekNumAcc;
                  }
              }



        }
    }

    private int getFirstDay(int dayOfWeek) {
        int ret = 0;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                ret = 0;
                break;
            case Calendar.MONDAY:
                ret = 1;
                break;
            case Calendar.TUESDAY:
                ret = 2;
                break;
            case Calendar.WEDNESDAY:
                ret = 3;
                break;
            case Calendar.THURSDAY:
                ret = 4;
                break;
            case Calendar.FRIDAY:
                ret = 5;
                break;
            case Calendar.SATURDAY:
                ret = 6;
                break;
        }
        return ret;
    }

    /**
     * Get first day of week as android.text.format.Time constant.
     * @return the first day of week in android.text.format.Time
     */
    public static int getFirstDayOfWeek() {
        int startDay = Calendar.getInstance().getFirstDayOfWeek();
        if (startDay == Calendar.SATURDAY) {
            return Time.SATURDAY;
        } else if (startDay == Calendar.MONDAY) {
            return Time.MONDAY;
        } else {
            return Time.SUNDAY;
        }
    }

    @Override
    //month에 해당하는 전체 일 개수(dya)
    public int getCount() {
        return 8*6;
    }

    //position에 해당하는 item 반환
    @Override
    public Object getItem(int position) {
        return monthItems[selectedPosition];
}

    //position에 해당하는 id 반환(여기서는 position 그대로 반환)
    @Override
    public long getItemId(int position) {
        return position;
    }

    //position에 해당하는 MonthItemView 반환. 화면에서 view가 사라지면 convertView에 넣어두었다가 다시 사용
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthItemView itemView;
        if(convertView == null){
            itemView = new MonthItemView(mContext);
        }
        else{
            itemView = (MonthItemView)convertView;
        }

        GridView.LayoutParams params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 50);

        int rowIndex = position/(numDayofWeek+1);
        int columnIndex = position%(numDayofWeek+1);

        //text가 0인 경우 text 변경


        itemView.setMonthItem(monthItems[position]);
        itemView.setLayoutParams(params);
        itemView.setPadding(2, 2, 2, 2);
        itemView.setGravity(Gravity.LEFT);

        //Sunday == red
        if (columnIndex == 1) {
            itemView.setTextColor(Color.RED);
        }else if (columnIndex == 7) {
            itemView.setTextColor(Color.BLUE);
        }else if(columnIndex == 0){
            itemView.setTextColor(Color.GREEN);
        }
        else {
            itemView.setTextColor(Color.BLACK);
        }


        //선택한 background 배경 변경
        if (position == getSelectedPosition()) {
            itemView.setBackgroundColor(Color.YELLOW);
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }

        return itemView;
    }

    //year, month에 해당하는 마지막 날짜 획득
    private int getMonthLastDay(int year, int month){
        switch (month) {
            case 0:     //month 1
            case 2:     //month 3
            case 4:
            case 6:     //month 7
            case 7:     //month 8
            case 9:     //month 10
            case 11:    //month 12
                return (31);    //짝수인 달에는 31일

            case 3:
            case 5:
            case 8:
            case 10:
                return (30);

            default:
                if(((year%4==0)&&(year%100!=0)) || (year%400==0) ) {
                    return (29);
                } else {
                    return (28);
                }
        }
    }

/*
    public long getCurMillisWithYearAndWeek(int year, int week){
        Calendar tempCal = Calendar.getInstance();
        tempCal.set(year, 0, 1);

        tempCal.set(Calendar.WEEK_OF_YEAR, week);
        //Log.d("calendCalc", "tttt " + tempCal.getTime());
        //Log.d("calendCalc", "Y:M:D : " + year + ":" + (tempCal.get(Calendar.MONTH)+1) + ":" + (tempCal.get(Calendar.DATE)-1) + ":" + tempCal.getTime() + ":"+ tempCal.getTimeInMillis());
        return tempCal.getTimeInMillis();
    }
    */

    public int getCurYear() {
        return currentYear;
    }

    public int getCurMonth() {
        return currentMonth;
    }

    public int getNumColumns() {
        return 7;
    }
}

