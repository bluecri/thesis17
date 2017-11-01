package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-10-25.
 */



@DatabaseTable(tableName = "historydatainnermarker")
public class HistoryDataInnerMarkerData {

    public final static String INNERMARKERDATA_ID_FIELD_NAME = "hdimd_innermarkerdata_id";
    public final static String HISTORYDATA_ID_FIELD_NAME = "hdimd_historydata_id";


    HistoryDataInnerMarkerData(){

    }

    public HistoryDataInnerMarkerData(HistoryData historyData, MarkerData markerData) {
        this.historyData = historyData;
        this.markerData = markerData;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = INNERMARKERDATA_ID_FIELD_NAME)
    private MarkerData markerData;

    @DatabaseField(foreign = true, columnName = HISTORYDATA_ID_FIELD_NAME)
    private HistoryData historyData;

    public MarkerData getMarkerData() {
        return markerData;
    }

    public void setMarkerData(MarkerData markerData) {
        this.markerData = markerData;
    }

    public HistoryData getHistoryData() {
        return historyData;
    }

    public void setHistoryData(HistoryData historyData) {
        this.historyData = historyData;
    }
}
