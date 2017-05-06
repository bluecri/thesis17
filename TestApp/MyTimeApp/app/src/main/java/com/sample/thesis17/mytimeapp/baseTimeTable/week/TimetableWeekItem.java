package com.sample.thesis17.mytimeapp.baseTimeTable.week;

/**
 * Created by kimz on 2017-05-06.
 */

public class TimetableWeekItem {
    private int iDayValue;
    private int iWeekValue;
    private boolean bWeek;
    public TimetableWeekItem(){

    }

    public TimetableWeekItem(int day, boolean bweek){
        iDayValue = day;
        //iWeekValue = week;
        bWeek = bweek;
    }
    public int getiDayValue(){
        return iDayValue;
    }
    //public int getiWeekValue() {return iWeekValue;}
    /*public void setiWeekValue(int week){
        this.iWeekValue = week;
    }*/
    public void setDayValue(int day){
        this.iDayValue = day;
    }
    public void setBWeek(boolean tf){
        this.bWeek = tf;
    }
    public boolean isWeek(){
        return this.bWeek;
    }
}
