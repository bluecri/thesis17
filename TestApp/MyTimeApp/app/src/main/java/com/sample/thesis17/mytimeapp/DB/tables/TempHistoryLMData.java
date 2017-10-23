package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-10-11.
 */


@DatabaseTable(tableName = "temphistorylmdata")
public class TempHistoryLMData {

    public final static String THLM_TEMPHISTORYDATA_ID_FIELD_NAME = "thlm_temphistorydata_id";
    public final static String THLM_LOCATIONMEMDATA_ID_FIELD_NAME = "thlm_locationmemorydata_id";


    TempHistoryLMData(){

    }

    public TempHistoryLMData(TempHistoryData tempHistoryData, LocationMemoryData locationMemoryData) {
        this.tempHistoryData = tempHistoryData;
        this.locationMemoryData = locationMemoryData;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = THLM_TEMPHISTORYDATA_ID_FIELD_NAME)
    private TempHistoryData tempHistoryData;

    @DatabaseField(foreign = true, columnName = THLM_LOCATIONMEMDATA_ID_FIELD_NAME)
    private LocationMemoryData locationMemoryData;

    public TempHistoryData getTempHistoryData() {
        return tempHistoryData;
    }

    public LocationMemoryData getLocationMemoryData() {
        return locationMemoryData;
    }
}
