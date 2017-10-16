package com.sample.thesis17.mytimeapp.DB.baseClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kimz on 2017-07-26.
 */

public class DatabaseHelperLocationMemory extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = Environment.getExternalStorageDirectory().getPath()+ File.separator +"timetable_location_memory.db";
    private static final int DATABASE_VERSION = 1;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);     //usage counter
    private static DatabaseHelperLocationMemory dbHelper= null;

    //DAO
    private Dao<LocationMemoryData, Integer> daoLocationMemoryData;

    //Constructor
    private DatabaseHelperLocationMemory(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //get DBHelper through this method
    public static synchronized DatabaseHelperLocationMemory getHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelperLocationMemory(context);
        }
        usageCounter.incrementAndGet();
        return dbHelper;
    }

    @Override
    public void close() {
        if(usageCounter.decrementAndGet() == 0){
            super.close();
            daoLocationMemoryData = null;
            dbHelper = null;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            //create table
            TableUtils.createTable(connectionSource, LocationMemoryData.class);
        }
        catch(SQLException e){
            Log.e("db", "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try{
            //drop table and recreate table.
            TableUtils.dropTable(connectionSource, LocationMemoryData.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        }
        catch(SQLException e){
            Log.e("db", "Unable to upgrade db " + i + " to " + i1, e);
        }
    }

    //getDao
    public Dao<LocationMemoryData, Integer> getDaoLocationMemoryData() throws SQLException{
        if(daoLocationMemoryData == null){
            daoLocationMemoryData = getDao(LocationMemoryData.class);
        }
        return daoLocationMemoryData;
    }
}
