package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */


@DatabaseTable(tableName = "markerTypeData")
public class MarkerTypeData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String strTypeName;

    @DatabaseField
    private String strMemo;

    public MarkerTypeData(){
        //empty
    }
}