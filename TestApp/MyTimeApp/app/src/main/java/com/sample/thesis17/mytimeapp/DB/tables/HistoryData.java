package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */


@DatabaseTable(tableName = "history")
public class HistoryData {
    public final static String HD_FTT_ID_FIELD_NAME = "hd_ftt_id";
    public final static String HD_MD_ID_FIELD_NAME = "hd_md_id";
    public final static String HD_MD_STARTTIME_FIELD_NAME = "hd_starttime_id";
    public final static String HD_MD_ENDTIME_FIELD_NAME = "hd_endtime_id";
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true, foreign = true, columnName = HD_FTT_ID_FIELD_NAME)
    private FixedTimeTableData foreFixedTimeTable;

    @DatabaseField(canBeNull = true, foreign = true, columnName = HD_MD_ID_FIELD_NAME)
    private MarkerData foreMarkerData;

    /*
    @DatabaseField(canBeNull = false, foreign = true)
    private MarkerData minDistMarker;
    */

    @DatabaseField(columnName = HD_MD_STARTTIME_FIELD_NAME)
    private long lStartTime;

    @DatabaseField(columnName = HD_MD_ENDTIME_FIELD_NAME)
    private long lEndTime;

    @DatabaseField
    private String memo;

    HistoryData(){
        // empty constructor is needed
    }

    public HistoryData(FixedTimeTableData foreFixedTimeTable, MarkerData foreMarkerData, long lStartTime, long lEndTime, String memo) {
        this.foreFixedTimeTable = foreFixedTimeTable;
        this.foreMarkerData = foreMarkerData;   //selected
        this.lStartTime = lStartTime;
        this.lEndTime = lEndTime;
        this.memo = memo;
        //this.minDistMarker = minDistMarker;
        //HistoryData를 가지고 있는 Loc 가져오는 query... : 필요없음.
        //inner marker list : HistoryDataInnerMarkerData == TempHistoryMarkerData
        //origin target locationMem Time Range : HistoryDataLocTimeRangeIncDec
    }

    /*
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
    */

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

    @Override
    public String toString() {
        return "HistoryData{" +
                "id=" + id +
                ", foreFixedTimeTable=" + foreFixedTimeTable +
                ", foreMarkerData=" + foreMarkerData +
                ", lStartTime=" + lStartTime +
                ", lEndTime=" + lEndTime +
                ", memo='" + memo + '\'' +
                '}';
    }
}