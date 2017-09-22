package com.sample.thesis17.mytimeapp.baseCalendar.week;

/**
 * Created by kimz on 2017-09-21.
 */

public class CalenderWeekItem {

    private int idx;    //HistoryData || TempHistoryData List의 index를 가리킴.
    boolean isHistoryData;

    public float left, top, right, bottom;
    private String text;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isHistoryData() {
        return isHistoryData;
    }

    public void setHistoryData(boolean historyData) {
        isHistoryData = historyData;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCoords(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;
    }
}


