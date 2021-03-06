package com.sample.thesis17.mytimeapp.DB.baseClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.types.IntegerObjectType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sample.thesis17.mytimeapp.DB.tables.ActiveListFixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.ActiveListMarker;
import com.sample.thesis17.mytimeapp.DB.tables.DateForTempHisoryData;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryDataInnerMarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryDataLocTimeRangeIncDecData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryLMData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryMarkerData;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kimz on 2017-07-26.
 */


public class DatabaseHelperMain extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "timetable_main.db";
    //private static final String DATABASE_NAME = Environment.getExternalStorageDirectory().getPath()+ File.separator+"timetable_main.db";
    private static final int DATABASE_VERSION = 1;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);     //usage counter
    private static DatabaseHelperMain dbHelper= null;

    //DAO
    private Dao<ActiveListFixedTimeTableData, Integer> daoActiveListFixedTimeTableData;
    private Dao<ActiveListMarker, Integer> daoActiveListMarker;
    private Dao<FixedTimeTableData, Integer> daoFixedTimeTableData;
    private Dao<HistoryData, Integer> daoHistoryData;
    private Dao<MarkerData, Integer> daoMarkerData;
    private Dao<MarkerTypeData, Integer> daoMarkerTypeData;
    private Dao<MarkerMarkerTypeData, Integer> daoMarkerMarkerTypeData;
    private Dao<TempHistoryData, Integer> daoTempHistoryData;
    private Dao<DateForTempHisoryData, Integer> daoDateForTempHisoryData;
    private Dao<TempHistoryLMData, Integer> daoTempHistoryLMData;
    private Dao<TempHistoryMarkerData, Integer> daoTempHistoryMarkerData;
    private Dao<HistoryDataInnerMarkerData, Integer> daoHistoryDataInnerMarkerData;
    private Dao<HistoryDataLocTimeRangeIncDecData, Integer> daoHistoryDataLocTimeRangeIncDecData;


    //Constructor
    private DatabaseHelperMain(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //get DBHelper through this method
    public static synchronized DatabaseHelperMain getHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelperMain(context);
        }
        usageCounter.incrementAndGet();
        return dbHelper;
    }

    @Override
    public void close() {
        if(usageCounter.decrementAndGet() == 0){
            super.close();
            daoActiveListFixedTimeTableData = null;
            daoActiveListMarker = null;
            daoFixedTimeTableData= null;
            daoHistoryData= null;
            daoMarkerData= null;
            daoMarkerTypeData = null;
            daoMarkerMarkerTypeData = null;
            daoTempHistoryData = null;
            daoDateForTempHisoryData = null;
            daoTempHistoryLMData = null;
            daoTempHistoryMarkerData = null;
            daoHistoryDataInnerMarkerData = null;
            daoHistoryDataLocTimeRangeIncDecData = null;
            dbHelper = null;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            //create table
            TableUtils.createTable(connectionSource, ActiveListFixedTimeTableData.class);
            TableUtils.createTable(connectionSource, ActiveListMarker.class);
            TableUtils.createTable(connectionSource, FixedTimeTableData.class);
            TableUtils.createTable(connectionSource, HistoryData.class);
            TableUtils.createTable(connectionSource, MarkerData.class);
            TableUtils.createTable(connectionSource, MarkerTypeData.class);
            TableUtils.createTable(connectionSource, MarkerMarkerTypeData.class);
            TableUtils.createTable(connectionSource, TempHistoryData.class);
            TableUtils.createTable(connectionSource, DateForTempHisoryData.class);
            TableUtils.createTable(connectionSource, TempHistoryLMData.class);
            TableUtils.createTable(connectionSource, TempHistoryMarkerData.class);
            TableUtils.createTable(connectionSource, HistoryDataInnerMarkerData.class);
            TableUtils.createTable(connectionSource, HistoryDataLocTimeRangeIncDecData.class);
        }
        catch(SQLException e){
            Log.e("db", "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try{
            //drop table and recreate table.
            TableUtils.dropTable(connectionSource, ActiveListFixedTimeTableData.class, true);
            TableUtils.dropTable(connectionSource, ActiveListMarker.class, true);
            TableUtils.dropTable(connectionSource, FixedTimeTableData.class, true);
            TableUtils.dropTable(connectionSource, HistoryData.class, true);
            TableUtils.dropTable(connectionSource, MarkerData.class, true);
            TableUtils.dropTable(connectionSource, MarkerTypeData.class, true);
            TableUtils.dropTable(connectionSource, MarkerMarkerTypeData.class, true);
            TableUtils.dropTable(connectionSource, TempHistoryData.class, true);
            TableUtils.dropTable(connectionSource, DateForTempHisoryData.class, true);
            TableUtils.dropTable(connectionSource, TempHistoryLMData.class, true);
            TableUtils.dropTable(connectionSource, TempHistoryMarkerData.class, true);
            TableUtils.dropTable(connectionSource, HistoryDataInnerMarkerData.class, true);
            TableUtils.dropTable(connectionSource, HistoryDataLocTimeRangeIncDecData.class, true);

            onCreate(sqLiteDatabase, connectionSource);
        }
        catch(SQLException e){
            Log.e("db", "Unable to upgrade db " + i + " to " + i1, e);
        }
    }


    //getDao
    public Dao<ActiveListFixedTimeTableData, Integer> getDaoActiveListFixedTimeTableData() throws SQLException{
        if(daoActiveListFixedTimeTableData == null){
            daoActiveListFixedTimeTableData = getDao(ActiveListFixedTimeTableData.class);
        }
        return daoActiveListFixedTimeTableData;
    }

    public Dao<ActiveListMarker, Integer> getDaoActiveListMarker() throws SQLException{
        if(daoActiveListMarker == null){
            daoActiveListMarker = getDao(ActiveListMarker.class);
        }
        return daoActiveListMarker;
    }

    public Dao<FixedTimeTableData, Integer> getDaoFixedTimeTableData() throws SQLException{
        if(daoFixedTimeTableData == null){
            daoFixedTimeTableData = getDao(FixedTimeTableData.class);
        }
        return daoFixedTimeTableData;
    }

    public Dao<HistoryData, Integer> getDaoHistoryData() throws SQLException{
        if(daoHistoryData == null){
            daoHistoryData = getDao(HistoryData.class);
        }
        return daoHistoryData;
    }

    public Dao<MarkerData, Integer> getDaoMarkerData() throws SQLException{
        if(daoMarkerData == null){
            daoMarkerData = getDao(MarkerData.class);
        }
        return daoMarkerData;
    }

    public Dao<MarkerTypeData, Integer> getDaoMarkerTypeData() throws SQLException{
        if(daoMarkerTypeData == null){
            daoMarkerTypeData = getDao(MarkerTypeData.class);
        }
        return daoMarkerTypeData;
    }

    public Dao<MarkerMarkerTypeData, Integer> getDaoMarkerMarkerTypeData() throws SQLException{
        if(daoMarkerMarkerTypeData == null){
            daoMarkerMarkerTypeData = getDao(MarkerMarkerTypeData.class);
        }
        return daoMarkerMarkerTypeData;
    }

    public Dao<TempHistoryData, Integer> getDaoTempHistoryData() throws SQLException{
        if(daoTempHistoryData == null){
            daoTempHistoryData = getDao(TempHistoryData.class);
        }
        return daoTempHistoryData;
    }

    public Dao<DateForTempHisoryData, Integer> getDaoDateForTempHisoryData() throws SQLException{
        if(daoDateForTempHisoryData == null){
            daoDateForTempHisoryData = getDao(DateForTempHisoryData.class);
        }
        return daoDateForTempHisoryData;
    }

    public Dao<TempHistoryLMData, Integer> getDaoTempHistoryLMData() throws SQLException{
        if(daoTempHistoryLMData == null){
            daoTempHistoryLMData = getDao(TempHistoryLMData.class);
        }
        return daoTempHistoryLMData;
    }

    public Dao<TempHistoryMarkerData, Integer> getDaoTempHistoryMarkerData() throws  SQLException{
        if(daoTempHistoryMarkerData == null){
            daoTempHistoryMarkerData = getDao(TempHistoryMarkerData.class);
        }
        return daoTempHistoryMarkerData;
    }

    public Dao<HistoryDataInnerMarkerData, Integer> getDaoHistoryDataInnerMarkerData() throws SQLException{
        if(daoHistoryDataInnerMarkerData == null){
            daoHistoryDataInnerMarkerData = getDao(HistoryDataInnerMarkerData.class);
        }
        return daoHistoryDataInnerMarkerData;
    }

    public Dao<HistoryDataLocTimeRangeIncDecData, Integer> getDaoHistoryDataLocTimeRangeIncDecData() throws SQLException{
        if(daoHistoryDataLocTimeRangeIncDecData == null){
            daoHistoryDataLocTimeRangeIncDecData = getDao(HistoryDataLocTimeRangeIncDecData.class);
        }
        return daoHistoryDataLocTimeRangeIncDecData;
    }

/*
    public Dao<MarkerTypeData, Integer> getDao() throws SQLException{
        if(daoMarkerTypeData == null){
            daoMarkerTypeData = getDao(MarkerTypeData.class);
        }
        return daoMarkerTypeData;
    }*/
}