package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */

@DatabaseTable(tableName = "activeListMarker")
public class ActiveListMarker {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private MarkerData foreMarkerData;

    public ActiveListMarker(){
        //empty
    }

    public ActiveListMarker(MarkerData foreMarkerData) {
        this.foreMarkerData = foreMarkerData;
    }

    public void setForeMarkerData(MarkerData markerData){
        foreMarkerData = markerData;
    }
    public MarkerData getForeMarkerData(){
        return foreMarkerData;
    }
}

