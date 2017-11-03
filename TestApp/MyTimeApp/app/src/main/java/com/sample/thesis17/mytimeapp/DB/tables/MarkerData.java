package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */

@DatabaseTable(tableName = "markerData")
public class MarkerData {
    public final static String ID_FIELD_NAME = "md_id";

    public MarkerData(double lat, double lng, String strMarkerName, double dRadius, double dInnerRadius, /*int iMarkerTypeBit,*/ String strMemo, boolean isCache, boolean isInvisible ) {
        this.lat = lat;
        this.lng = lng;
        this.strMarkerName = strMarkerName;
        this.dRadius = dRadius;
        this.dInnerRadius = dInnerRadius;
        //this.iMarkerTypeBit = iMarkerTypeBit;
        this.strMemo = strMemo;
        this.isCache = isCache;
        this.isInvisible = isInvisible;
    }

    MarkerData(){
        // empty constructor is needed
    }

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
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

    //@DatabaseField
    //private int iMarkerTypeBit;

    @DatabaseField
    private String strMemo;

    @DatabaseField
    private boolean isCache;

    @DatabaseField
    private boolean isInvisible;




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

    /*
    public void setIMarkerTypeBit(int iMarkerTypeBit){
        this.iMarkerTypeBit = iMarkerTypeBit;
    }
    public int setIMarkerTypeBit(){
        return iMarkerTypeBit;
    }
    */


    public void setStrMemo(String strMemo){
        this.strMemo = strMemo;
    }
    public String getStrMemo(){
        return strMemo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkerData)) return false;

        MarkerData that = (MarkerData) o;

        return id == that.id;

    }

    @Override
    public String toString() {
        return "MarkerData{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                ", strMarkerName='" + strMarkerName + '\'' +
                ", dRadius=" + dRadius +
                ", dInnerRadius=" + dInnerRadius +
                ", strMemo='" + strMemo + '\'' +
                ", isCache=" + isCache +
                ", isInvisible=" + isInvisible +
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }
}