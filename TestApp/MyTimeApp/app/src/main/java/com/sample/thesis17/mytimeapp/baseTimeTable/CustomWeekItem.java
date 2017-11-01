package com.sample.thesis17.mytimeapp.baseTimeTable;

import android.graphics.Color;

/**
 * Created by kimz on 2017-09-18.
 */

public class CustomWeekItem {
    private int idx;    //List의 TimeTableData의 index를 가리킴.
    public float left, top, right, bottom;
    private String text;
    private int blockColor;
    private int textColor;
    private long startTime = 0;
    private long endTime = 0;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setCoords(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;
    }

    public int getBlockColor() {
        return blockColor;
    }

    public void setBlockColor(int blockColor) {
        this.blockColor = blockColor;
    }


    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CustomWeekItem{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
