package com.sample.thesis17.mytimeapp.DB.baseClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sample.thesis17.mytimeapp.DB.tables.ActiveListFixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.ActiveListMarker;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kimz on 2017-07-26.
 */


public class DatabaseHelperMain extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "timetable_main.db";
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
/*
    public Dao<MarkerTypeData, Integer> getDao() throws SQLException{
        if(daoMarkerTypeData == null){
            daoMarkerTypeData = getDao(MarkerTypeData.class);
        }
        return daoMarkerTypeData;
    }*/
}