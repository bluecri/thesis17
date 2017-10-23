package com.sample.thesis17.mytimeapp.locationS;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperLocationMemory;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;
import com.sample.thesis17.mytimeapp.broadcaseRecv.WifiInfoBroadcastReceiver;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static java.lang.Thread.sleep;

//dataLMForSave is not need to be created with 'New'

public class locationService extends Service {

    Handler hMainHandler = null;

    LocationManager locationManager;        //location manager
    LocationManager locationManagerGps;        //location manager
    LocationListener locationListenerCustom;        //location listener
    LocationListener getLocationListenerCustomGps;

    boolean isLocationMAnagerRunning = false;       //locationManager is running or not
    boolean isHaveToUseNetworkProvider = false;     //use networkProvider or not. Do not change this during service is running.

    String strCurrentUsingNetwork = null;   //current network type

    //config from activity
    boolean isUseWifi = false;
    boolean isUseGps = false;
    boolean isUsedata = false;

    long iInterval = 10*60*1000;    //10분
    long lGpsWaitMillis = 1000 * 40;    //40초

    private DatabaseHelperLocationMemory databaseHelperLocationMemory = null;

    LocationMemoryData dataLMForGpsThread =  null;
    LocationMemoryData dataLMForSave = null;

    Thread locationThread = null;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(AllConstants.START_LOCATION_FOREGROUND_ACTION)){
            startForeground(1, new Notification());

            //(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)

            setByteUseConfig(intent.getIntExtra("iFlagUseConfig", 0));
            settingCurrentNetworkAndNetworkProvider();      //get current network Info 0> strCurrentUsingNetwork, isHaveToUseNetworkProvider

            Log.d("locationService", "init currentUsingNetwork " + strCurrentUsingNetwork + " ++ " + isHaveToUseNetworkProvider);

            iInterval = (long)(intent.getIntExtra("iInterval", 10) * 60 * 1000);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerGps = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Log.d("test", "서비스의 onStartCommand");


            hMainHandler = new HandlerGpsLocationRequest();


            startLocationService();

            return super.onStartCommand(intent, flags, startId);
        }
        else if(intent.getAction().equals(AllConstants.STOP_LOCATION_FOREGROUND_ACTION)){
            stopForeground(true);
            locationManager.removeUpdates(locationListenerCustom);
            locationManagerGps.removeUpdates(getLocationListenerCustomGps);
            strCurrentUsingNetwork = null;
            stopSelf();
        }
        //changed in network connection from WifiInfoBroadcastReceiver
        else if(intent.getAction().equals(AllConstants.NETWORK_STATUS_CHANGED_ACTION)){
            checkNetworkChangeAndRestartServiceOrNot(intent);
        }
        //서비스가 종료 된 경우, START_STICKY : null intent로 서비스 재시작 / START_NOT_STICKY : 서비스 재시작 x /
        // START_REDELIVER_INTETN : 이전의 intent를 이용해 재시작
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        locationManager.removeUpdates(locationListenerCustom);
        if(locationThread != null && locationThread.isAlive()){
            locationThread.interrupt();
            locationManagerGps.removeUpdates(getLocationListenerCustomGps);
        }
        if(databaseHelperLocationMemory != null){
            databaseHelperLocationMemory.close();
            databaseHelperLocationMemory = null;
        }

        hMainHandler = null;

        super.onDestroy();
    }

    private void startLocationService() {
        Log.d("locationService", "startLocationService");
        locationListenerCustom = new LocationListenerNet();
        getLocationListenerCustomGps = new LocationListenerGps();
        dataLMForGpsThread = new LocationMemoryData(0.0, 0.0, 0, (float)0.0, null, null, 0);
        // minDistance = 0;

        try {
            if(isHaveToUseNetworkProvider){
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        iInterval,
                        0,
                        locationListenerCustom);
                Log.d("locationService", "network listener running..." + String.valueOf(iInterval));
            }
            else if(isUseGps){
                //use only gps
                if(locationThread != null){
                    locationThread.interrupt();
                    locationThread = null;
                }
                locationThread = new Thread(new ThreadLocationMemoryDataProcessWithOnlyGps());
                locationThread.start();
            }

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        isLocationMAnagerRunning = true;
    }

    private class LocationListenerNet implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            float accuracy = location.getAccuracy();

            dataLMForSave = new LocationMemoryData(latitude, longitude, System.currentTimeMillis() + LONG_HOUR_MILLIS * 9, accuracy, null, null, 0); //for LOCALE_US

            Log.d("locatoinService", "new thread created..");
            locationThread = new Thread(new ThreadLocationMemoryDataProcessWithGps());
            locationThread.start();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    //restart location service.
    private void restartLocationService(boolean b){
        Log.d("locationService", "restartLocationService");
        locationManagerGps.removeUpdates(getLocationListenerCustomGps);
        if(locationThread.isAlive()){
            locationThread.interrupt();
            locationThread = null;
        }
        locationManager.removeUpdates(locationListenerCustom);
        isHaveToUseNetworkProvider = b;
        startLocationService();
    }

    //process : check network change and restart service or not
    private void checkNetworkChangeAndRestartServiceOrNot(Intent intent){
        if(isLocationMAnagerRunning) {
            String strNewUsingNetwork = intent.getStringExtra("strNetworkStatus");
            Log.d("locationService", "networkStatusChanged " + strCurrentUsingNetwork + " -> " + strNewUsingNetwork);
            Log.d("locationService", "" + isUseWifi + " -- " + isUsedata);
            if (!strNewUsingNetwork.equals(strCurrentUsingNetwork)) {
                if (strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN)) {
                    if (isUseWifi && !isUsedata) {    //use only wifi -> on
                        //on
                        //isHaveToUseNetworkProvider = true;
                        Log.d("locationService", "case1");
                        restartLocationService(true);
                    }
                } else if (strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_DATA_CONN)) {
                    if (strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_NO_CONN) && isUseWifi && isUsedata) {
                        //on
                        //isHaveToUseNetworkProvider = true;
                        Log.d("locationService", "case2");
                        restartLocationService(true);
                    } else if (strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN) && isUseWifi && !isUsedata) {
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case3");
                        restartLocationService(false);
                    }
                } else if (strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_NO_CONN)) {
                    if (strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN) && isUseWifi) {
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case4");
                        restartLocationService(false);
                    } else if (strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_DATA_CONN) && isUseWifi && isUsedata) {
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case5");
                        restartLocationService(false);
                    }
                }
                strCurrentUsingNetwork = strNewUsingNetwork;    //update strCurrentUsingNetwork !
            }
        }
    }


    //get byte and setting config byte.
    private void setByteUseConfig(int b){
        Log.d("locationService", String.valueOf(b));
        if((b & AllConstants.USEGPS_FLAG) == AllConstants.USEGPS_FLAG){
            isUseGps = true;
            Log.d("locationService", "UseGps");
        }
        if((b & AllConstants.USEWIFI_FLAG) == AllConstants.USEWIFI_FLAG){
            isUseWifi = true;
            Log.d("locationService", "UseWifi");
        }
        if((b & AllConstants.USEDATA_FLAG) == AllConstants.USEDATA_FLAG){
            isUsedata = true;
            Log.d("locationService", "UseData");
        }
    }


    //init strCurrentUsingNetwork and isHaveToUseNetworkProvider
    private void settingCurrentNetworkAndNetworkProvider(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = connectivityManager.getActiveNetworkInfo();
        isHaveToUseNetworkProvider = false;
        if(nInfo != null){
            if((nInfo.getType() == ConnectivityManager.TYPE_WIFI) && nInfo.isConnectedOrConnecting()){
                Log.d("WifiInfoBroad", "wifi");
                strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_WIFI_CONN;
                if(isUseWifi){
                    isHaveToUseNetworkProvider = true;
                }

            }
            else if((nInfo.getType() == ConnectivityManager.TYPE_MOBILE) && nInfo.isConnectedOrConnecting()){
                Log.d("WifiInfoBroad", "mobile");
                strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_DATA_CONN;
                if(isUsedata){
                    isHaveToUseNetworkProvider = true;
                }
            }
            else{
                Log.d("WifiInfoBroad", "???");
                strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_NO_CONN;
                isHaveToUseNetworkProvider = false;
            }
        }
        else{
            //no connection
            Log.d("WifiInfoBroad", "no conn");
            strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_NO_CONN;
            isHaveToUseNetworkProvider = false;
        }
    }

    private DatabaseHelperLocationMemory getDatabaseHelperLocationMemory(){
        if(databaseHelperLocationMemory == null){
            databaseHelperLocationMemory = DatabaseHelperLocationMemory.getHelper(this);
        }
        return databaseHelperLocationMemory;
    }

    private class ThreadLocationMemoryDataProcessWithGps implements Runnable{
        @Override
        public void run() {
            Log.d("locationService", "thread run");
            try{
                if(isUseGps){
                    Log.d("locationService", "is use gps");
                    hMainHandler.sendEmptyMessage(0);   // run locationManager.requestUpdates(getLocationListenerCustomGps);
                    sleep(lGpsWaitMillis);
                    Log.d("locationService", "sleep End");
                    locationManagerGps.removeUpdates(getLocationListenerCustomGps);    //if therer is no getLocationListenerCustomGps update, mListeners.remove return null.. Therefore, can be called with same param again.

                }
                if(!Thread.currentThread().isInterrupted()){
                    if(isDataLMForGpsThreadWritten()){
                        locationManagerGps.removeUpdates(getLocationListenerCustomGps);
                        //compare gps vs network accuracy
                        Log.d("locationService","gps acc : " + String.valueOf(dataLMForGpsThread.getfAccur()) + " / net acc : " + String.valueOf(dataLMForSave.getfAccur()));

                        if(dataLMForGpsThread.getfAccur() - dataLMForSave.getfAccur() < 0.01){
                            //use gps for writing
                            Log.d("locationService", "use Gps for writing!");
                            Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
                            daoLocationMemoryDataInteger.create(dataLMForGpsThread);
                            debugPrintLMData(dataLMForGpsThread);
                            debugPrintDAOInfo(daoLocationMemoryDataInteger);
                        }
                        else{
                            //use network for writing
                            Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
                            daoLocationMemoryDataInteger.create(dataLMForSave);
                            //debugPrintLMData(dataLMForGpsThread);
                            debugPrintDAOInfo(daoLocationMemoryDataInteger);
                        }
                    }
                    else{
                        //write only with network info. [cannot use GPS because user is in indoor || not use GPS(!isUseGps) ]
                        //use network for writing
                        Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
                        daoLocationMemoryDataInteger.create(dataLMForSave);
                        //debugPrintLMData(dataLMForGpsThread);
                        debugPrintDAOInfo(daoLocationMemoryDataInteger);
                    }
                }
                else{
                    //interrupted
                }

            }
            catch(SecurityException e){
                Log.e("locationService", "threadLocationMemoryDataProcessWithGps security exception", e);   //because of locationManager
            }
            catch(InterruptedException e){
                Log.e("locationService", "threadLocationMemoryDataProcessWithGps Interrupted exception", e);    //due to Thread Interrupt
            }
            catch(SQLException e){
                Log.e("locationService", "threadLocationMemoryDataProcessWithGps SQLExcption", e);
            }
            finally{
                dataLMForGpsThread.setfAccur((float)0.0);   //init mark
                locationManagerGps.removeUpdates(getLocationListenerCustomGps);
                //close DB
            }
        }
    }

    private class ThreadLocationMemoryDataProcessWithOnlyGps implements Runnable{
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                Log.d("locationService", "thread run while..");
                try{
                    if(isUseGps){
                        Log.d("locationService", "onlywithgps isusegps");
                        hMainHandler.sendEmptyMessage(0);   // run locationManager.requestUpdates(getLocationListenerCustomGps);
                        sleep(lGpsWaitMillis);
                        Log.d("locationService", "onlywithgps end sleep");
                        locationManagerGps.removeUpdates(getLocationListenerCustomGps);    //if there is no getLocationListenerCustomGps update, mListeners.remove return null.. Therefore, can be called with same param again.
                        Log.d("locationService", "onlywithgps removeUpdates");
                    }
                    if(!Thread.currentThread().isInterrupted()){
                        if(isDataLMForGpsThreadWritten()) {
                            locationManagerGps.removeUpdates(getLocationListenerCustomGps);
                            Log.d("locationService", "use Gps");
                            if (dataLMForGpsThread.getfAccur() < 100.0) {
                                //use gps for writing
                                Log.d("locationService", "use Gps for writing!");
                                Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
                                daoLocationMemoryDataInteger.create(dataLMForGpsThread);
                                debugPrintLMData(dataLMForGpsThread);
                                debugPrintDAOInfo(daoLocationMemoryDataInteger);
                            }
                        }
                    }
                    sleep(iInterval);
                }
                catch(SecurityException e){
                    Log.e("locationService", "threadLocationMemoryDataProcessWithGps security exception", e);   //because of locationManager
                }
                catch(InterruptedException e){
                    Log.e("locationService", "threadLocationMemoryDataProcessWithGps Interrupted exception", e);    //due to Thread Interrupt
                }
                catch(SQLException e){
                    Log.e("locationService", "threadLocationMemoryDataProcessWithGps SQLExcption", e);
                }
                finally{
                    dataLMForGpsThread.setfAccur((float)0.0);   //init mark
                    locationManagerGps.removeUpdates(getLocationListenerCustomGps);
                    //close DB
                }

            }
        }
    }

    private class LocationListenerGps implements LocationListener {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
        @Override
        public void onLocationChanged(Location location) {
            dataLMForGpsThread.setLat(location.getLatitude());
            dataLMForGpsThread.setLng(location.getLongitude());
            dataLMForGpsThread.setfAccur(location.getAccuracy());
            dataLMForGpsThread.setlMillisTimeWritten(System.currentTimeMillis() + LONG_HOUR_MILLIS * 9); //for LOCALE_US
        }
    }

    private void debugPrintDAOInfo(Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger){
        try{
            if(daoLocationMemoryDataInteger != null){
                List<LocationMemoryData> listLMData = daoLocationMemoryDataInteger.queryForAll();
                StringBuilder strbListLmData = new StringBuilder();
                for (LocationMemoryData data : listLMData){
                    Date date = new Date(data.getlMillisTimeWritten());
                    DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                    String strDateFormatted = formatter.format(date);
                    strbListLmData.append("lat : ").append(data.getLat()).append("..lng : ").append(data.getLng()).append("..time : ").append(strDateFormatted).append("..Accur : ").append(data.getfAccur());
                }
                Log.d("locationService", strbListLmData.toString());
            }
        }
        catch(SQLException e){
            Log.d("locationService", "debugPrintDAOInfo SQL exception");
        }


    }

    private void debugPrintLMData(LocationMemoryData lmData){
        String msg = "위도 : " + String.valueOf(lmData.getLng()) + "\n경도:" + String.valueOf(lmData.getLat()) + "\n정확도 : " + String.valueOf(lmData.getfAccur());
        //textView.setText("location changed... : " + latitude + ", " + longitude);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //gpsData가 채워져있는가?
    private boolean isDataLMForGpsThreadWritten(){
        if(dataLMForGpsThread.getfAccur() == (float)0.0){
            return false;
        }
        else
            return true;
    }

    //handler
    private class HandlerGpsLocationRequest extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Log.d("locationService", "handleMessage start");
                if (locationManagerGps != null) {
                    Log.d("locationService", "handleMessage requestLocationUpdates");
                    locationManagerGps.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            iInterval,
                            0,
                            getLocationListenerCustomGps);
                }
            }
            catch(SecurityException e){
                Log.d("locationService", "security exception in Handler");
            }

        }
    }

}
