package com.sample.thesis17.mytimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.SharedElementCallback;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperLocationMemory;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.baseCalendar.month.CalenderMonthFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.week.CalenderWeekFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.day.TimetableDayFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.week.TimetableWeekFragment;
import com.sample.thesis17.mytimeapp.locationS.SettingFragment;
import com.sample.thesis17.mytimeapp.map.MapsActivity;
import com.sample.thesis17.mytimeapp.setting.SettingActivity;
import com.sample.thesis17.mytimeapp.statistic.StatisticItemFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CalenderMonthFragment.CalenderMonthFragmentListener, CalenderWeekFragment.CalenderWeekFragmentListener, TimetableWeekFragment.TimetableWeekFragmentListener {

    CalenderWeekFragment calenderWeekFragment = null;
    CalenderMonthFragment calenderMonthFragment = null;
    TimetableDayFragment timetableDayFragment = null;
    StatisticItemFragment statisticFragment = null;
    MainBlankFragment blankFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelperMain = getDatabaseHelperMain();
        databaseHelperLocationMemory = getDatabaseHelperLocationMemory();
        refreshDB();
        //sqliteExport();       //copy db to sdcard

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        blankFragment = MainBlankFragment.newInstance("", "");
        fragmentTransaction.replace(R.id.mainFragmentContainer, blankFragment, "blankMainFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(calenderWeekFragment != null && fragmentManager.findFragmentById(R.id.mainFragmentContainer) instanceof CalenderWeekFragment){
            // move to Month of Week
            long tempStartTimeLong = calenderWeekFragment.getStartLongTimeOfFragmentArgument();
            //Log.d("debbugged", "tempStarttimeLong : " + tempStartTimeLong);
            fragmentChangeToMonthView(tempStartTimeLong);
        }
        else if(fragmentManager.findFragmentById(R.id.mainFragmentContainer) instanceof TimetableDayFragment){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //create Calender fragment with parameter
            TimetableWeekFragment timetableFragment = TimetableWeekFragment.newInstance("", "");
            //replace mainFragment's fragment -> calender(TAG : timetable)
            //fragmentTransaction.replace(R.id.mainFragmentContainer, timetableFragment, "timetable");
            fragmentTransaction.replace(R.id.mainFragmentContainer, timetableFragment, "timetable_week_fragment");
            fragmentTransaction.commit();
            timetableDayFragment = null;
        }
        else if(!(fragmentManager.findFragmentById(R.id.mainFragmentContainer) instanceof MainBlankFragment)){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            blankFragment = MainBlankFragment.newInstance("", "");
            fragmentTransaction.replace(R.id.mainFragmentContainer, blankFragment, "blankMainFragment");
            fragmentTransaction.commit();
        }
        else{
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //timetable
        if (id == R.id.nav_main_calendar) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(calenderMonthFragment == null && calenderWeekFragment == null){
                calenderMonthFragment = CalenderMonthFragment.newInstance(System.currentTimeMillis());
                fragmentTransaction.replace(R.id.mainFragmentContainer, calenderMonthFragment, "calenderMonthFragment");
                fragmentTransaction.commit();
                Log.d("mainActivity", "both null");
            }
            else if(calenderWeekFragment != null){
                //calenderWeekFragment = CalenderMonthFragment.newInstance(System.currentTimeMillis());
                fragmentTransaction.replace(R.id.mainFragmentContainer, calenderWeekFragment, "calenderWeekFragment");
                fragmentTransaction.commit();
            }
            else if(calenderMonthFragment != null){ //always true
                fragmentTransaction.replace(R.id.mainFragmentContainer, calenderMonthFragment, "calenderMonthFragment");
                fragmentTransaction.commit();
                Log.d("mainActivity", "month frag not null");
            }
        }
        //calendar
        else if (id == R.id.nav_main_timetable) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            //fragment Transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //create Calender fragment with parameter
            TimetableWeekFragment timetableFragment = TimetableWeekFragment.newInstance("", "");
            //replace mainFragment's fragment -> calender(TAG : timetable)
            //fragmentTransaction.replace(R.id.mainFragmentContainer, timetableFragment, "timetable");
            fragmentTransaction.replace(R.id.mainFragmentContainer, timetableFragment, "timetable_week_fragment");
            fragmentTransaction.commit();
        }
        //map
        else if (id == R.id.nav_main_map) {
            if(blankFragment == null){
                blankFragment = MainBlankFragment.newInstance("", "");
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragmentContainer, blankFragment, "blankMainFragment");
            fragmentTransaction.commit();
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        }
        //Setting
        else if (id == R.id.nav_manage) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SettingFragment settingFragment = SettingFragment.newInstance("", "");
            fragmentTransaction.replace(R.id.mainFragmentContainer, settingFragment, "setting");
            fragmentTransaction.commit();
        }

        else if (id == R.id.nav_share)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if(statisticFragment == null){
                statisticFragment = new StatisticItemFragment();
            }

            fragmentTransaction.replace(R.id.mainFragmentContainer, statisticFragment, "statisticFragment");
            fragmentTransaction.commit();

        }

        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void fragmentChangeToWeekView(long startTime){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        calenderWeekFragment = CalenderWeekFragment.newInstance(startTime);
        //Log.d("debbugged", "CalenderWeekFragment() start" + startTime);
        fragmentTransaction.replace(R.id.mainFragmentContainer, calenderWeekFragment, "calenderWeekFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void fragmentChangeToMonthView(long inTime) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        calenderMonthFragment = CalenderMonthFragment.newInstance(inTime);
        fragmentTransaction.replace(R.id.mainFragmentContainer, calenderMonthFragment, "calenderMonthFragment");
        fragmentTransaction.commit();
        calenderWeekFragment = null;
    }

    public MarkerData testFunc(MarkerData lists){
        lists.setLng(111.0);
        return lists;
    }

    public void refreshDB(){

        try{
            //모든 locationMem bind data null로
            Dao<LocationMemoryData, Integer> locationMemoryDataIntegerDao = databaseHelperLocationMemory.getDaoLocationMemoryData();
            List<LocationMemoryData> locationMemoryDataList = locationMemoryDataIntegerDao.queryForAll();
            for(LocationMemoryData fttd : locationMemoryDataList){
                /*fttd.setBindedTempHistoryData(null);
                fttd.setBindedHistoryData(null);
                fttd.setbDummy(0);
                locationMemoryDataIntegerDao.update(fttd);
                */
                Log.d("mainactivity", "LM : " + fttd.toString());
            }
            Dao<FixedTimeTableData, Integer> tegerDao = databaseHelperMain.getDaoFixedTimeTableData();
            List<FixedTimeTableData> locationFixedTimeTableDataList = tegerDao.queryForAll();
            for(FixedTimeTableData fttd : locationFixedTimeTableDataList){
                //fttd.setBindedTempHistoryData(null);
                //fttd.setBindedHistoryData(null);
                //locationMemoryDataIntegerDao.update(fttd);
                Log.d("mainactivity", "FX : " + fttd.toString());
            }
            Dao<MarkerData, Integer> daoMarkerData = databaseHelperMain.getDaoMarkerData();
            List<MarkerData> markerDataList = daoMarkerData.queryForAll();
            for(MarkerData md : markerDataList){
                Log.d("mainactivity", "md : " + md.toString());
            }

            Dao<HistoryData, Integer> historyDataIntegerDao = databaseHelperMain.getDaoHistoryData();
            List<HistoryData> historyDataList = historyDataIntegerDao.queryForAll();
            for(HistoryData hd : historyDataList){
                Log.d("mainactivity", "HD : " + hd.toString());
            }
        }
        catch(SQLException e){
            Log.d("mainActivity", "SQLException mainActivity");
        }

    }

    private DatabaseHelperLocationMemory databaseHelperLocationMemory = null;
    private DatabaseHelperMain databaseHelperMain = null;

    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(this);
        }
        return databaseHelperMain;
    }


    private DatabaseHelperLocationMemory getDatabaseHelperLocationMemory(){
        if(databaseHelperLocationMemory == null){
            databaseHelperLocationMemory = DatabaseHelperLocationMemory.getHelper(this);
        }
        return databaseHelperLocationMemory;
    }

    private <T> void  deleteAllWithDao(Dao<T, Integer> inDao){
        try{
            List<T> locationMemoryDataList = inDao.queryForAll();
            for(T fttd : locationMemoryDataList){
                inDao.delete(fttd);
            }
        }
        catch(SQLException e){
            Log.d("mainActivity", "mainActivity deleteFunctionQuery exception : " + e.toString());
        }

    }
    //DB 추출
    public void sqliteExport(){
        try {
            File sdDir = Environment.getExternalStorageDirectory();
            File dataDir = Environment.getDataDirectory();

            if(sdDir.canWrite()){
                String crDBPath = "//data//com.sample.thesis17.mytimeapp//databases//timetable_location_memory.db";
                String bkDBPath = "timetable_location_memory.sqlite";

                File crDB = new File(dataDir, crDBPath);
                File bkDB = new File(sdDir, bkDBPath);

                Log.d("coppp", dataDir.toString());
                Log.d("coppp", sdDir.toString());

                if(crDB.exists()){
                    FileChannel src = new FileInputStream(crDB).getChannel();
                    FileChannel dst = new FileOutputStream(bkDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Log.d("coppp", "crdb exixt");
                }

                if(bkDB.exists()){
                    Log.d("coppp", "bkDB exixt");
                    Toast.makeText(this, "DB file Export Success!", Toast.LENGTH_SHORT).show();
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void replaceDayFragmentWithTime(long longStartTime) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        timetableDayFragment = TimetableDayFragment.newInstance(longStartTime);
        fragmentTransaction.replace(R.id.mainFragmentContainer, timetableDayFragment, "timetable_day_fragment");
        fragmentTransaction.commit();
    }
}
