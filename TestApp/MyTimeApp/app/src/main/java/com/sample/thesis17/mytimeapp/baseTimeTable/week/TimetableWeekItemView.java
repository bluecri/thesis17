package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sample.thesis17.mytimeapp.baseCalendar.month.MonthItem;

/**
 * Created by kimz on 2017-05-06.
 */

public class TimetableWeekItemView extends AppCompatTextView {
    //MonthItemView가 가지고 있는 monthItem
    private TimetableWeekItem timetableWeekItem;

    //기본 생성자들
    public TimetableWeekItemView(Context context) {
        super(context);
        init();
    }

    public TimetableWeekItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimetableWeekItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //기본 배경 white
        setBackgroundColor(Color.WHITE);
    }

    public void setMonthItem(TimetableWeekItem timetableItem){
        this.timetableWeekItem = timetableItem;
        //monthItem의 day를 가져와 text를 설정한다.
        setText(String.valueOf(timetableItem.getiDayValue()));
    }
    public TimetableWeekItem getMonthItem(){
        return this.timetableWeekItem;
    }
}
