package com.sample.thesis17.mytimeapp.DB.tables;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Random;

/**
 * Created by kimz on 2017-07-26.
 */



@DatabaseTable(tableName = "fixedTimeTableData")
public class FixedTimeTableData {
    public final static String FIXEDTIMETABLE_ID_FIELD_NAME = "fixedtimetable_id";
    public final static String FIXEDTIMETABLE_MARKERDATA_FIELD_NAME = "fixedtimetable_markerdata";
    //public final static String LOCATIONMEMORY_BINDEDTEMPHISTORYDATA_FIELD_NAME = "locationmem_temphistory";
    @DatabaseField(generatedId = true, columnName = FIXEDTIMETABLE_ID_FIELD_NAME)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = FIXEDTIMETABLE_MARKERDATA_FIELD_NAME)
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

    @DatabaseField
    private boolean isInvisible;


    FixedTimeTableData() {
        // empty constructor is needed
    }

    public FixedTimeTableData(MarkerData foreMarkerData, String strFixedTimeTableName, long lStartTime, long lEndTime, long lBoundStartTime, long lBoundEndTime, long lInnerBoundStartTime, long lInnerBoundEndTime, String strMemo, boolean isCache, boolean isInvisible) {
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
        this.isInvisible = isInvisible;
    }


    public MarkerData getForeMarkerData() {
        return foreMarkerData;
    }

    public void setForeMarkerData(MarkerData foreMarkerData) {
        this.foreMarkerData = foreMarkerData;
    }

    public String getStrFixedTimeTableName() {
        return strFixedTimeTableName;
    }

    public void setStrFixedTimeTableName(String strFixedTimeTableName) {
        this.strFixedTimeTableName = strFixedTimeTableName;
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

    public long getlBoundStartTime() {
        return lBoundStartTime;
    }

    public void setlBoundStartTime(long lBoundStartTime) {
        this.lBoundStartTime = lBoundStartTime;
    }

    public long getlBoundEndTime() {
        return lBoundEndTime;
    }

    public void setlBoundEndTime(long lBoundEndTime) {
        this.lBoundEndTime = lBoundEndTime;
    }

    public long getlInnerBoundStartTime() {
        return lInnerBoundStartTime;
    }

    public void setlInnerBoundStartTime(long lInnerBoundStartTime) {
        this.lInnerBoundStartTime = lInnerBoundStartTime;
    }

    public long getlInnerBoundEndTime() {
        return lInnerBoundEndTime;
    }

    public void setlInnerBoundEndTime(long lInnerBoundEndTime) {
        this.lInnerBoundEndTime = lInnerBoundEndTime;
    }

    public String getStrMemo() {
        return strMemo;
    }

    public void setStrMemo(String strMemo) {
        this.strMemo = strMemo;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void setInvisible(boolean invisible) {
        isInvisible = invisible;
    }

    public int getColor(){
        Random rnd = new Random(lStartTime);
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    /*
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
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedTimeTableData)) return false;

        FixedTimeTableData that = (FixedTimeTableData) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}