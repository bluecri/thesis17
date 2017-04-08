package com.sample.thesis17.locationtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class locationService extends Service {

    LocationManager locationManager;
    LocationListener locationListenerCustom;
    String strProvider = "";
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
            strProvider = intent.getStringExtra("strProvider");
            long iCollectInterval = intent.getLongExtra("iCollectInterval", 10000);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Log.d("test", "서비스의 onStartCommand");
            startLocationService(iCollectInterval);
            return super.onStartCommand(intent, flags, startId);
        }
        else if(intent.getAction().equals(AllConstants.STOP_LOCATION_FOREGROUND_ACTION)){
            stopForeground(true);
            locationManager.removeUpdates(locationListenerCustom);
            stopSelf();
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
        locationListenerCustom = new locationListener();
        float minDistance = 0;

        try {
            //use locationManager's LOCATION_SERVICE and set listener
            locationManager.requestLocationUpdates(
                    strProvider,
                    minTime,
                    minDistance,
                    locationListenerCustom);

            //first location msg and textView
            Location lastLocation = locationManager.getLastKnownLocation(strProvider);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                //textView.setText("위/경도 : " + latitude + ", " + longitude);
                Toast.makeText(getApplicationContext(), "최근위치 : " + "위도: " + latitude + "\n경도:" + longitude, Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private class locationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

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

}
