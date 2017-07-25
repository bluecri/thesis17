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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sample.thesis17.mytimeapp.broadcaseRecv.WifiInfoBroadcastReceiver;

public class locationService extends Service {

    LocationManager locationManager;
    LocationListener locationListenerCustom;
    //String strProvider = "";

    boolean isLocationMAnagerRunning = false;
    boolean isHaveToUseNetworkProvider = false;

    String strCurrentUsingNetwork = null;

    boolean isUseWifi = false;
    boolean isUseGps = false;
    boolean isUsedata = false;

    long iInterval = 10*60*1000;    //10분

    //static int iCollectInterval;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(AllConstants.START_LOCATION_FOREGROUND_ACTION)){
            startForeground(1, new Notification());

            //(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
            //strProvider = intent.getStringExtra("strProvider");

            //TODO : get current network Info 0> strCurrentUsingNetwork, isHaveToUseNetworkProvider


            setByteUseConfig(intent.getIntExtra("iFlagUseConfig", 0));
            settingCurrentNetworkAndNetworkProvider();

            iInterval = (long)(intent.getIntExtra("iInterval", 10) * 60 * 1000);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Log.d("test", "서비스의 onStartCommand");
            startLocationService(iInterval);

            return super.onStartCommand(intent, flags, startId);
        }
        else if(intent.getAction().equals(AllConstants.STOP_LOCATION_FOREGROUND_ACTION)){
            stopForeground(true);
            locationManager.removeUpdates(locationListenerCustom);
            strCurrentUsingNetwork = null;
            stopSelf();
        }
        //changed in network connection from WifiInfoBroadcastReceiver
        else if(intent.getAction().equals(AllConstants.NETWORK_STATUS_CHANGED_ACTION) && (isLocationMAnagerRunning == true)){

            String strNewUsingNetwork = intent.getStringExtra("strNetworkStatus");
            Log.d("locationService", "networkStatusChanged " + strCurrentUsingNetwork + " -> " + strNewUsingNetwork);
            Log.d("locationService", "" + isUseWifi + " -- " + isUsedata);
            if(strNewUsingNetwork.equals(strCurrentUsingNetwork)){
                //do nothing
            }
            else{
                if(strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN)){
                    if(isUseWifi && !isUsedata){    //use only wifi -> on
                        //on
                        //isHaveToUseNetworkProvider = true;
                        Log.d("locationService", "case1");
                        restartLocationService(true);
                    }
                }
                else if(strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_DATA_CONN)){
                    if(strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_NO_CONN) && isUseWifi && isUsedata){
                        //on
                        //isHaveToUseNetworkProvider = true;
                        Log.d("locationService", "case2");
                        restartLocationService(true);
                    }
                    else if(strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN) && isUseWifi && !isUsedata){
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case3");
                        restartLocationService(false);
                    }
                }
                else if(strNewUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_NO_CONN)){
                    if(strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_WIFI_CONN) && isUseWifi){
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case4");
                        restartLocationService(false);
                    }
                    else if(strCurrentUsingNetwork.equals(WifiInfoBroadcastReceiver.STR_DATA_CONN) && isUseWifi && isUsedata){
                        //off
                        //isHaveToUseNetworkProvider = false;
                        Log.d("locationService", "case5");
                        restartLocationService(false);
                    }
                }
                strCurrentUsingNetwork = strNewUsingNetwork;    //update strCurrentUsingNetwork !
            }


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
        super.onDestroy();
    }

    private void startLocationService(long minTime) {
        Log.d("locationService", "startLocationService");
        locationListenerCustom = new locationListener();
        float minDistance = 0;

        try {
            //use locationManager's LOCATION_SERVICE and set listener
            if(isUseGps){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        minTime,
                        minDistance,
                        locationListenerCustom);
            }
            if(isHaveToUseNetworkProvider){
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        minTime,
                        minDistance,
                        locationListenerCustom);
            }



            //first location msg and textView
            /*Location lastLocation = locationManager.getLastKnownLocation(strProvider);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                //textView.setText("위/경도 : " + latitude + ", " + longitude);
                Toast.makeText(getApplicationContext(), "최근위치 : " + "위도: " + latitude + "\n경도:" + longitude, Toast.LENGTH_LONG).show();
            }*/
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        isLocationMAnagerRunning = true;
    }

    private class locationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            float accuracy = location.getAccuracy();

            String msg = "위도 : " + latitude + "\n경도:" + longitude;

            //textView.setText("location changed... : " + latitude + ", " + longitude);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    private void restartLocationService(boolean b){
        Log.d("locationService", "restartLocationService");
        locationManager.removeUpdates(locationListenerCustom);
        isHaveToUseNetworkProvider = b;
        startLocationService(iInterval);
    }

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


    private void settingCurrentNetworkAndNetworkProvider(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = connectivityManager.getActiveNetworkInfo();
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
                strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_WIFI_CONN;
                if(isUsedata){
                    isHaveToUseNetworkProvider = true;
                }
            }
            else{
                Log.d("WifiInfoBroad", "???");
                strCurrentUsingNetwork = WifiInfoBroadcastReceiver.STR_WIFI_CONN;
            }
        }
        else{
            //no connection
            Log.d("WifiInfoBroad", "no conn");
        }
    }


}
