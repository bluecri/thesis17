package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-10-11.
 */


@DatabaseTable(tableName = "temphistorylmdata")
public class TempHistoryMarkerData {

    public final static String THLM_TEMPHISTORYDATA_ID_FIELD_NAME = "temphistorydata_id";
    public final static String THLM_MARKERDATA_ID_FIELD_NAME = "markerdata_id";


    TempHistoryMarkerData(){

    }

    public TempHistoryMarkerData(TempHistoryData tempHistoryData, MarkerData markerData) {
        this.tempHistoryData = tempHistoryData;
        this.markerData = markerData;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = THLM_TEMPHISTORYDATA_ID_FIELD_NAME)
    private TempHistoryData tempHistoryData;

    @DatabaseField(foreign = true, columnName = THLM_MARKERDATA_ID_FIELD_NAME)
    private MarkerData markerData;

    public TempHistoryData getTempHistoryData() {
        return tempHistoryData;
    }

    public MarkerData getMarkerData() {
        return markerData;
    }
}