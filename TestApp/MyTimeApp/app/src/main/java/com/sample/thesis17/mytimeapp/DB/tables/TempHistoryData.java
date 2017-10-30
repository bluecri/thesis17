package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-09-21.
 */


@DatabaseTable(tableName = "tempHistory")
public class TempHistoryData {
    public final static String TH_TEMPHISTORYDATA_STARTTIME_FIELD_NAME = "temphistorydata_starttime";
    public final static String TH_TEMPHISTORYDATA_ENDTIME_FIELD_NAME = "temphistorydata_endtime";
    public final static String TH_TEMPHISTORYDATA_FTT_FIELD_NAME = "temphistorydata_fft";
    public final static String TH_TEMPHISTORYDATA_MD_FIELD_NAME = "temphistorydata_md";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true, foreign = true, columnName = TH_TEMPHISTORYDATA_FTT_FIELD_NAME)
    private FixedTimeTableData foreFixedTimeTable;

    @DatabaseField(canBeNull = true, foreign = true, columnName = TH_TEMPHISTORYDATA_MD_FIELD_NAME)
    private MarkerData foreMarkerData;

    /*
    @DatabaseField(canBeNull = false, foreign = true)
    private MarkerData minDistMarkerData;
    */

    @DatabaseField(columnName = TH_TEMPHISTORYDATA_STARTTIME_FIELD_NAME)
    private long lStartTime;

    @DatabaseField(columnName = TH_TEMPHISTORYDATA_ENDTIME_FIELD_NAME)
    private long lEndTime;

    @DatabaseField
    private double tempLat;

    @DatabaseField
    private double tempLng;

    @DatabaseField
    private String memo;


    TempHistoryData() {
        // empty constructor is needed
    }
/*
    public TempHistoryData(FixedTimeTableData foreFixedTimeTable, MarkerData foreMarkerData, long lStartTime, long lEndTime, String memo, double tempLat, double tempLng) {
        this.foreFixedTimeTable = foreFixedTimeTable;
        this.foreMarkerData = foreMarkerData;
        //this.minDistMarkerData = minDistMarkerData;
        this.lStartTime = lStartTime;
        this.lEndTime = lEndTime;
        this.memo = memo;
        this.tempLat = tempLat;
        this.tempLng = tempLng;
    }*/

    public TempHistoryData(FixedTimeTableData foreFixedTimeTable, MarkerData foreMarkerData, long lStartTime, long lEndTime, double tempLat, double tempLng, String memo) {
        this.foreFixedTimeTable = foreFixedTimeTable;
        this.foreMarkerData = foreMarkerData;
        this.lStartTime = lStartTime;
        this.lEndTime = lEndTime;
        this.tempLat = tempLat;
        this.tempLng = tempLng;
        this.memo = memo;
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

    public double getTempLat() {
        return tempLat;
    }

    public void setTempLat(double tempLat) {
        this.tempLat = tempLat;
    }

    public double getTempLng() {
        return tempLng;
    }

    public void setTempLng(double tempLng) {
        this.tempLng = tempLng;
    }

    /*
    public MarkerData getMinDistMarkerData() {
        return minDistMarkerData;
    }

    public void setMinDistMarkerData(MarkerData minDistMarkerData) {
        this.minDistMarkerData = minDistMarkerData;
    }
    */


    @Override
    public String toString() {
        return "TempHistoryData{" +
                "id=" + id +
                ", foreFixedTimeTable=" + foreFixedTimeTable +
                ", foreMarkerData=" + foreMarkerData +
                ", lStartTime=" + lStartTime +
                ", lEndTime=" + lEndTime +
                ", tempLat=" + tempLat +
                ", tempLng=" + tempLng +
                ", memo='" + memo + '\'' +
                '}';
    }
}