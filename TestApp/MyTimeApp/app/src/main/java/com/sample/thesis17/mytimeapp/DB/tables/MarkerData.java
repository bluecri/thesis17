package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */

@DatabaseTable(tableName = "markerData")
public class MarkerData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lng;

    @DatabaseField
    private String strMarkerName;

    @DatabaseField
    private double dRadius;

    @DatabaseField
    private double dInnerRadius;

    @DatabaseField
    private int iMarkerTypeBit;

    @DatabaseField
    private String strMemo;

    @DatabaseField
    private boolean isCache;


    MarkerData(){
        // empty constructor is needed
    }

    /*
    public MarkerData(double lat, double lng, long lMillisTimeWritten) {
        this.lMillisTimeWritten = lMillisTimeWritten;
        this.lat = lat;
        this.lng = lng;
    }
    */

    public int getId(){
        return id;
    }

    public void setId(){
        // none
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public long getLillisTimeWritten(){
        return lMillisTimeWritten;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

    public void setLMillisTimeWritten(long lMillisTimeWritten){
        this.lMillisTimeWritten = lMillisTimeWritten;
    }
}