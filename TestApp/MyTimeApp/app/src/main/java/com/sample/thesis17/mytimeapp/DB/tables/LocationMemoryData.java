package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by kimz on 2017-07-26.
 */

@DatabaseTable(tableName = "locationMemory")
public class LocationMemoryData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lng;

    @DatabaseField
    private long lMillisTimeWritten;

    @DatabaseField
    private float fAccur;

    @DatabaseField(canBeNull = true, foreign = true)
    private HistoryData bindedHistoryData;

    @DatabaseField(canBeNull = true, foreign = true)
    private TempHistoryData bindedTempHistoryData;

    @DatabaseField
    private boolean bDummy;


    LocationMemoryData(){
        // empty constructor is needed
    }

    public LocationMemoryData(double lat, double lng, long lMillisTimeWritten, float fAccur, HistoryData bindedHistoryData, TempHistoryData bindedTempHistoryData, boolean bDummy) {
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

    public boolean isbDummy() {
        return bDummy;
    }

    public void setbDummy(boolean bDummy) {
        this.bDummy = bDummy;
    }
}
