package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */


@DatabaseTable(tableName = "activeListFixedTimeTableData")
public class ActiveListFixedTimeTableData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private FixedTimeTableData foreFixedTimeTable;

    public ActiveListFixedTimeTableData(){
        //empty
    }

    public void setForeFixedTimeTable(FixedTimeTableData fixedTimeTableData){
        foreFixedTimeTable = fixedTimeTableData;
    }
    public FixedTimeTableData getForeFixedTimeTable(){
        return foreFixedTimeTable;
    }
}

