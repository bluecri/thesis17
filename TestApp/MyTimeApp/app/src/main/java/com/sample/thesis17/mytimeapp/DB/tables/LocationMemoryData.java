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

    LocationMemoryData(){
        // empty constructor is needed
    }

    public LocationMemoryData(double lat, double lng, long lMillisTimeWritten) {
        this.lMillisTimeWritten = lMillisTimeWritten;
        this.lat = lat;
        this.lng = lng;
    }

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
