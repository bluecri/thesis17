package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-09-21.
 */


@DatabaseTable(tableName = "tempHistory")
public class TempHistoryData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private FixedTimeTableData foreFixedTimeTable;

    @DatabaseField(canBeNull = false, foreign = true)
    private MarkerData foreMarkerData;

    @DatabaseField
    private long lStartTime;

    @DatabaseField
    private long lEndTime;

    @DatabaseField
    private String memo;

    TempHistoryData() {
        // empty constructor is needed
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FixedTimeTableData getForeFixedTimeTable() {
        return foreFixedTimeTable;
    }

    public void setForeFixedTimeTable(FixedTimeTableData foreFixedTimeTable) {
        this.foreFixedTimeTable = foreFixedTimeTable;
    }

    public MarkerData getForeMarkerData() {
        return foreMarkerData;
    }

    public void setForeMarkerData(MarkerData foreMarkerData) {
        this.foreMarkerData = foreMarkerData;
    }

    public long getlStartTime() {
        return lStartTime;
    }

    public void setlStartTime(long lStartTime) {
        this.lStartTime = lStartTime;
    }

    public long getlEndTime() {
        return lEndTime;
    }

    public void setlEndTime(long lEndTime) {
        this.lEndTime = lEndTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}