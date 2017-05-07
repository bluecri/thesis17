package com.sample.thesis17.mytimeapp.baseTimeTable.week;

/**
 * Created by kimz on 2017-05-07.
 */

public class Point {
    public float fCol;
    public float fRow;
    Point(float x, float y){
        fCol = x;
        fRow = y;
    }
    public void setFPointColRow(float fCol, float fRow){
        this.fCol = fCol;
        this.fRow = fRow;
    }
    public void setFPointCol(float fCol){
        this.fCol = fCol;
    }
    public void setFPointRow(float fRow){
        this.fRow = fRow;
    }

    public float getFPointCol(){
        return fCol;
    }
    public float getFPointRow(){
        return fRow;
    }
}
