package com.sample.thesis17.mytimeapp.baseCalendar.month;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.sample.thesis17.mytimeapp.R;

/**
 * Created by kimz on 2017-05-01.
 */

public class MonthItemView extends AppCompatTextView{
    //TODO : AppCompatTextView Style

    //MonthItemView가 가지고 있는 monthItem
    private MonthItem monthItem;

    //기본 생성자들
    public MonthItemView(Context context) {
        super(context);
        init(context);
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MonthItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        //기본 배경 white
        this.setPadding(2, 2, 2, 2);
        //this.setTextColor(Color.WHITE);
        this.setGravity(Gravity.CENTER);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_round_item));
        } else {
            this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.textview_round_item));
        }

        //setBackgroundColor(Color.WHITE);
    }

    public void setMonthItem(MonthItem monthItem){
        this.monthItem = monthItem;
        //monthItem의 day를 가져와 text를 설정한다.
        if(monthItem.getiDayValue() == 0){
            setText("");
        }
        else{
            setText(String.valueOf(monthItem.getiDayValue()));
        }

    }
    public MonthItem getMonthItem(){
        return this.monthItem;
    }
}
