package com.sample.thesis17.mytimeapp.baseCalendar;

/**
 * Created by kimz on 2017-09-21.
 */

public class CalenderWeekItemIdxWithIsHistoryData {
    private int idx;
    private boolean isHistory;

    public CalenderWeekItemIdxWithIsHistoryData(int idx, boolean isHistory) {
        this.idx = idx;
        this.isHistory = isHistory;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }
}
