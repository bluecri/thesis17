package com.sample.thesis17.mytimeapp.baseTimeTable;

/**
 * Created by kimz on 2017-05-09.
 */

public class Points {

    public float fCol;
    public float fRow;
    public Points(){
        fCol = 0;
        fRow = 0;
    }
    public Points(float x, float y){
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
