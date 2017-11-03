package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by kimz on 2017-07-26.
 */



@DatabaseTable(tableName = "locationMemory")
public class LocationMemoryData {
    public final static String LOCATIONMEMORY_ID_FIELD_NAME = "locationmem_id";
    public final static String LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME = "locationmem_history";
    public final static String LOCATIONMEMORY_BINDEDTEMPHISTORYDATA_FIELD_NAME = "locationmem_temphistory";
    public final static String LOCATIONMEMORY_BDUMMY_FIELD_NAME = "locationmem_dummy";
    public final static String LOCATIONMEMORY_TIMEWRITTEN_FIELD_NAME = "locationmem_writtentime";

    @DatabaseField(generatedId = true, columnName = LOCATIONMEMORY_ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lng;

    @DatabaseField(columnName = LOCATIONMEMORY_TIMEWRITTEN_FIELD_NAME)
    private long lMillisTimeWritten;

    @DatabaseField
    private float fAccur;

    @DatabaseField(canBeNull = true, foreign = true, columnName = LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME)
    private HistoryData bindedHistoryData;

    @DatabaseField(canBeNull = true, foreign = true, columnName = LOCATIONMEMORY_BINDEDTEMPHISTORYDATA_FIELD_NAME)
    private TempHistoryData bindedTempHistoryData;

    @DatabaseField(columnName = LOCATIONMEMORY_BDUMMY_FIELD_NAME)
    private int bDummy;


    LocationMemoryData(){
        // empty constructor is needed
    }

    public LocationMemoryData(double lat, double lng, long lMillisTimeWritten, float fAccur, HistoryData bindedHistoryData, TempHistoryData bindedTempHistoryData, int bDummy) {
        this.lMillisTimeWritten = lMillisTimeWritten;
        this.lat = lat;
        this.lng = lng;
        this.fAccur = fAccur;
        this.bindedHistoryData = bindedHistoryData;
        this.bindedTempHistoryData = bindedTempHistoryData;
        this.bDummy = bDummy;
    }

    public int getId(){
        return id;
    }

    public void setId(){
        // none
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getlMillisTimeWritten() {
        return lMillisTimeWritten;
    }

    public void setlMillisTimeWritten(long lMillisTimeWritten) {
        this.lMillisTimeWritten = lMillisTimeWritten;
    }

    public float getfAccur() {
        return fAccur;
    }

    public void setfAccur(float fAccur) {
        this.fAccur = fAccur;
    }

    public HistoryData getBindedHistoryData() {
        return bindedHistoryData;
    }

    public void setBindedHistoryData(HistoryData bindedHistoryData) {
        this.bindedHistoryData = bindedHistoryData;
    }

    public TempHistoryData getBindedTempHistoryData() {
        return bindedTempHistoryData;
    }

    public void setBindedTempHistoryData(TempHistoryData bindedTempHistoryData) {
        this.bindedTempHistoryData = bindedTempHistoryData;
    }

    public int getbDummy() {
        return bDummy;
    }

    public void setbDummy(int bDummy) {
        this.bDummy = bDummy;
    }

    @Override
    public String toString() {
        return "LocationMemoryData{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                ", lMillisTimeWritten=" + lMillisTimeWritten +
                ", fAccur=" + fAccur +
                ", bindedHistoryData=" + bindedHistoryData +
                ", bindedTempHistoryData=" + bindedTempHistoryData +
                ", bDummy=" + bDummy +
                '}';
    }
}
