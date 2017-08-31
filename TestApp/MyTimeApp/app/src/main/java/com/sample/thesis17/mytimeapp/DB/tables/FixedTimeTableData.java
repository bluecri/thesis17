package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */



@DatabaseTable(tableName = "fixedTimeTableData")
public class FixedTimeTableData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private MarkerData foreMarkerData;

    @DatabaseField
    private String strFixedTimeTableName;   //시간표 main name

    @DatabaseField
    private long lStartTime;

    @DatabaseField
    private long lEndTime;

    @DatabaseField
    private long lBoundStartTime;   //사용자 지정 bound

    @DatabaseField
    private long lBoundEndTime;

    @DatabaseField
    private long lInnerBoundStartTime;  //app 내부 bound

    @DatabaseField
    private long lInnerBoundEndTime;

    @DatabaseField
    private String strMemo;

    @DatabaseField
    private boolean isCache;


    FixedTimeTableData() {
        // empty constructor is needed
    }

    public FixedTimeTableData(MarkerData foreMarkerData, String strFixedTimeTableName, long lStartTime, long lEndTime, long lBoundStartTime, long lBoundEndTime, long lInnerBoundStartTime, long lInnerBoundEndTime, String strMemo, boolean isCache) {
        this.foreMarkerData = foreMarkerData;
        this.strFixedTimeTableName = strFixedTimeTableName;
        this.lStartTime = lStartTime;
        this.lEndTime = lEndTime;
        this.lBoundStartTime = lBoundStartTime;
        this.lBoundEndTime = lBoundEndTime;
        this.lInnerBoundStartTime = lInnerBoundStartTime;
        this.lInnerBoundEndTime = lInnerBoundEndTime;
        this.strMemo = strMemo;
        this.isCache = isCache;
    }

    public void setForeMarkerData(MarkerData markerData){
        foreMarkerData = markerData;
    }
    public MarkerData getForeMarkerData(){
        return foreMarkerData;
    }

    public void setStrFixedTimeTableName(String strFixedTimeTableName){
        this.strFixedTimeTableName = strFixedTimeTableName;
    }
    public String getStrFixedTimeTableName(){
        return strFixedTimeTableName;
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

    public void setLBoundStartTime(Long lBoundStartTime){
        this.lBoundStartTime = lBoundStartTime;
    }
    public long GetLBoundStartTime(){
        return lBoundStartTime;
    }

    public void setLBoundEndTime(Long lBoundEndTime){
        this.lBoundEndTime = lBoundEndTime;
    }
    public long GetLBoundEndTime(){
        return lBoundEndTime;
    }

    public void setLInnerBoundStartTime(Long lInnerBoundStartTime){
        this.lInnerBoundStartTime = lInnerBoundStartTime;
    }
    public long getLInnerBoundStartTime(){
        return lInnerBoundStartTime;
    }

    public void setLInnerBoundEndTime(Long lInnerBoundEndTime){
        this.lInnerBoundEndTime = lInnerBoundEndTime;
    }
    public long getLInnerBoundEndTime(){
        return lInnerBoundEndTime;
    }

    public void setStrMemo(String strMemo){
        this.strMemo = strMemo;
    }
    public String getStrMemo(){
        return strMemo;
    }

    public void setIsCache(boolean isCache){
        this.isCache = isCache;
    }
    public boolean getIsCache(){
        return isCache;
    }




}