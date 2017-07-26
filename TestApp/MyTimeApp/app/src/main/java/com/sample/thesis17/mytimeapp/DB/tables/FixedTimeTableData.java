package com.sample.thesis17.mytimeapp.DB.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */



@DatabaseTable(tableName = "fixedTimeTableData")
public class FixedTimeTableData {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private int foreMarker;

    @DatabaseField
    private String strFixedTimeTableName;   //시간표 main name

    @DatabaseField
    private long lStartTime;

    @DatabaseField
    private long lEndTime;

    @DatabaseField
    private long lBoundStartTime;   //사용자 지정 bound

    @DatabaseField
    private long lBoundEndTime;

    @DatabaseField
    private long lInnerBoundStartTime;  //app 내부 bound

    @DatabaseField
    private long lInnerBoundEndTime;

    @DatabaseField
    private String strMemo;

    @DatabaseField
    private boolean isCache;


    FixedTimeTableData() {
        // empty constructor is needed
    }
}