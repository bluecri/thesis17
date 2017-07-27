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

    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
    public void setLat(double lat){
        this.lat = lat;
    }
    public void setLng(double lng){
        this.lng = lng;
    }

    public void setStrMarkerName(String strMarkerName){
        this.strMarkerName = strMarkerName;
    }
    public String getStrMarkerName(){
        return strMarkerName;
    }

    public void setDRadius(double dRadius){
        this.dRadius = dRadius;
    }
    public double getDRadius(){
        return dRadius;
    }

    public void setDInnerRadius(double dInnerRadius){
        this.dInnerRadius = dInnerRadius;
    }
    public double getDInnerRadius(){
        return dInnerRadius;
    }

    public void setIMarkerTypeBit(int iMarkerTypeBit){
        this.iMarkerTypeBit = iMarkerTypeBit;
    }
    public int setIMarkerTypeBit(){
        return iMarkerTypeBit;
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