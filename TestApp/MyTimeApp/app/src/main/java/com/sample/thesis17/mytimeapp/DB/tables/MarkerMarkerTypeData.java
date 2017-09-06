package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-09-05.
 */

@DatabaseTable(tableName = "markermarkertype")
public class MarkerMarkerTypeData {

    public final static String MARKERDATA_ID_FIELD_NAME = "markerdata_id";
    public final static String MARKERTYPEDATA_ID_FIELD_NAME = "markertypedata_id";


    MarkerMarkerTypeData(){

    }

    public MarkerMarkerTypeData(MarkerData markerData, MarkerTypeData markerTypeData) {
        this.markerData = markerData;
        this.markerTypeData = markerTypeData;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = MARKERDATA_ID_FIELD_NAME)
    private MarkerData markerData;

    @DatabaseField(foreign = true, columnName = MARKERTYPEDATA_ID_FIELD_NAME)
    private MarkerTypeData markerTypeData;

    public MarkerData getMarkerData() {
        return markerData;
    }

    public MarkerTypeData getMarkerTypeData() {
        return markerTypeData;
    }
}
