package com.sample.thesis17.mytimeapp.baseCalendar.month;

/**
 * Created by kimz on 2017-05-01.
 */

public class MonthItem {
    private int iDayValue;
    private long lWeekValue;
    private boolean bWeek;
    public MonthItem(){

    }

    public MonthItem(int day, boolean bweek){
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

    public long getlWeekValue() {
        return lWeekValue;
    }

    public void setlWeekValue(long lWeekValue) {
        this.lWeekValue = lWeekValue;
    }
}
