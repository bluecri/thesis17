package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-10-11.
 */


@DatabaseTable(tableName = "temphistorymarkerdata")
public class TempHistoryMarkerData {

    public final static String THMM_TEMPHISTORYDATA_ID_FIELD_NAME = "thmm_temphistorydata_id";
    public final static String THMM_MARKERDATA_ID_FIELD_NAME = "thmm_markerdata_id";


    TempHistoryMarkerData(){

    }

    public TempHistoryMarkerData(TempHistoryData tempHistoryData, MarkerData markerData) {
        this.tempHistoryData = tempHistoryData;
        this.markerData = markerData;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = THMM_TEMPHISTORYDATA_ID_FIELD_NAME)
    private TempHistoryData tempHistoryData;

    @DatabaseField(foreign = true, columnName = THMM_MARKERDATA_ID_FIELD_NAME)
    private MarkerData markerData;

    public TempHistoryData getTempHistoryData() {
        return tempHistoryData;
    }

    public MarkerData getMarkerData() {
        return markerData;
    }
}