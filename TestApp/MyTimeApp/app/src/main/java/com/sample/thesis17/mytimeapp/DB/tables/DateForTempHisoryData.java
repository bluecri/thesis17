package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-09-21.
 */

@DatabaseTable(tableName = "dateForTempHistoryData")
public class DateForTempHisoryData {
    public final static String DATEFORTEMPHISTORYDATA_ID_FIELD_NAME = "datefortemphistorydata_id";
    public final static String DATEFORTEMPHISTORYDATA_LONGWEEK_FIELD_NAME = "datefortemphistorydata_longweek";

    @DatabaseField(generatedId = true, columnName = DATEFORTEMPHISTORYDATA_ID_FIELD_NAME)
    private int id;

    @DatabaseField(index = true, columnName = DATEFORTEMPHISTORYDATA_LONGWEEK_FIELD_NAME)
    private long lFirstDayOfWeek;

    public long getlFirstDayOfWeek() {
        return lFirstDayOfWeek;
    }

    public void setlFirstDayOfWeek(long lFirstDayOfWeek) {
        this.lFirstDayOfWeek = lFirstDayOfWeek;
    }

}
