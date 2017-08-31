package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */


@DatabaseTable(tableName = "history")
public class HistoryData {
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

    HistoryData(){
        // empty constructor is needed
    }

    public HistoryData(FixedTimeTableData foreFixedTimeTable, MarkerData foreMarkerData, long lStartTime, long lEndTime) {
        this.foreFixedTimeTable = foreFixedTimeTable;
        this.foreMarkerData = foreMarkerData;
        this.lStartTime = lStartTime;
        this.lEndTime = lEndTime;
    }

    public void setForeFixedTimeTable(FixedTimeTableData fixedTimeTableData){
        foreFixedTimeTable = fixedTimeTableData;
    }
    public FixedTimeTableData getForeFixedTimeTable(){
        return foreFixedTimeTable;
    }

    public void setForeMarkerData(MarkerData markerData){
        foreMarkerData = markerData;
    }
    public MarkerData getForeMarkerData(){
        return foreMarkerData;
    }

    public void setLStartTime(Long lStartTime){
        this.lStartTime = lStartTime;
    }
    public long GetLStartTime(){
        return lStartTime;
    }

    public void setLEndTime(Long lEndTime){
        this.lEndTime = lEndTime;
    }
    public long GetLEndtTime(){
        return lEndTime;
    }

}