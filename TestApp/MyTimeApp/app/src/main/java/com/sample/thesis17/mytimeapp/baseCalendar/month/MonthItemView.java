package com.sample.thesis17.mytimeapp.baseCalendar.month;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by kimz on 2017-05-01.
 */

public class MonthItemView extends AppCompatTextView{

    //MonthItemView가 가지고 있는 monthItem
    private MonthItem monthItem;

    //기본 생성자들
    public MonthItemView(Context context) {
        super(context);
        init();
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //기본 배경 white
        setBackgroundColor(Color.WHITE);
    }

    public void setMonthItem(MonthItem monthItem){
        this.monthItem = monthItem;
        //monthItem의 day를 가져와 text를 설정한다.
        setText(String.valueOf(monthItem.getiDayValue()));
    }
    public MonthItem getMonthItem(){
        return this.monthItem;
    }
}
