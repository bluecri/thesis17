package com.sample.thesis17.mytimeapp.baseCalendar;

import android.util.Log;

import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;

import java.util.List;

/**
 * Created by kimz on 2017-09-25.
 */

public class LocationGroup {
    List<LocationMemoryData> listLMD;
    double centerLat, centerLng;
    FixedTimeTableData targetFixedTimeTableData;
    MarkerData targetMarkerData;
    List<MarkerData> listInnerMarkerData;

    public LocationGroup(List<LocationMemoryData> listLMD, double centerLat, double centerLng, FixedTimeTableData targetFixedTimeTableData, MarkerData targetMarkerData, List<MarkerData> listInnerMarkerData) {
        this.listLMD = listLMD;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.targetFixedTimeTableData = targetFixedTimeTableData;
        this.targetMarkerData = targetMarkerData;
        this.listInnerMarkerData = listInnerMarkerData;
    }

    public List<LocationMemoryData> getListLMD() {
        return listLMD;
    }

    public void setListLMD(List<LocationMemoryData> listLMD) {
        this.listLMD = listLMD;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    public FixedTimeTableData getTargetFixedTimeTableData() {
        return targetFixedTimeTableData;
    }

    public void setTargetFixedTimeTableData(FixedTimeTableData targetFixedTimeTableData) {
        this.targetFixedTimeTableData = targetFixedTimeTableData;
    }

    public MarkerData getTargetMarkerData() {
        return targetMarkerData;
    }

    public void setTargetMarkerData(MarkerData targetMarkerData) {
        this.targetMarkerData = targetMarkerData;
    }

    public List<MarkerData> getListInnerMarkerData() {
        return listInnerMarkerData;
    }

    public void setListInnerMarkerData(List<MarkerData> listInnerMarkerData) {
        this.listInnerMarkerData = listInnerMarkerData;
    }

    public long getFirstTimeOfGroup(){
        List<LocationMemoryData> tempLMDList = getListLMD();
        try{
            if(tempLMDList != null){
                return tempLMDList.get(0).getlMillisTimeWritten();
            }
            else{
                return -1;
            }
        }
        catch(IndexOutOfBoundsException e){
            Log.d("LocationGroup", "getFirstTimeOfGroup error");
            return -1;
        }
    }

    public long getLastTimeOfGroup(){
        List<LocationMemoryData> tempLMDList = getListLMD();
        try{
            if(tempLMDList != null){
                return tempLMDList.get(tempLMDList.size()-1).getlMillisTimeWritten();
            }
            else{
                return -1;
            }
        }
        catch(IndexOutOfBoundsException e){
            Log.d("LocationGroup", "getFirstTimeOfGroup error");
            return -1;
        }
    }
}
