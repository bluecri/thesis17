package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-10-25.
 */

@DatabaseTable(tableName = "historyDataLocTimeRangeIncDecData")
public class HistoryDataLocTimeRangeIncDecData {
    public final static String ID_FIELD_NAME = "hdltridd_id";
    public final static String HISTORYDATA_FIELD_NAME = "hdltridd_historydata";


    HistoryDataLocTimeRangeIncDecData() {
        // empty constructor is needed
    }

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField(foreign = true, columnName = HISTORYDATA_FIELD_NAME)
    private HistoryData targetHistoryData;

    @DatabaseField
    private long startInc;

    @DatabaseField
    private long endInc;

    public HistoryDataLocTimeRangeIncDecData(HistoryData targetHistoryData, long startInc, long endInc) {
        this.targetHistoryData = targetHistoryData;
        this.startInc = startInc;
        this.endInc = endInc;
    }

    public HistoryData getTargetHistoryData() {
        return targetHistoryData;
    }

    public void setTargetHistoryData(HistoryData targetHistoryData) {
        this.targetHistoryData = targetHistoryData;
    }

    public long getStartInc() {
        return startInc;
    }

    public void setStartInc(long startInc) {
        this.startInc = startInc;
    }

    public long getEndInc() {
        return endInc;
    }

    public void setEndInc(long endInc) {
        this.endInc = endInc;
    }
}