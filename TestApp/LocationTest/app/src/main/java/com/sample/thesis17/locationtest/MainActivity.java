package com.sample.thesis17.locationtest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textViewBelow;
    EditText editText;
    RadioButton radButtonNetwork;
    RadioButton radButtonGps;
    //locationListener locationListenerCustom;
    LocationManager locationManager;

    boolean bStart = false;
    long iCollectInterval = 10000;
    String strProvider = null;
    String stateString = null;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textview);
        textViewBelow = (TextView) findViewById(R.id.textViewBelow);
        Button button = (Button) findViewById(R.id.button);
        radButtonNetwork = (RadioButton) findViewById(R.id.radioNetwork);
        radButtonGps = (RadioButton) findViewById(R.id.radioGps);

        editText = (EditText)findViewById(R.id.editText);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        editText.setText(String.valueOf(10000));    //init interval to 10000

        //앱 실행시 초기값 설정
        strProvider = LocationManager.NETWORK_PROVIDER;
        radButtonNetwork.setChecked(true);
        if(isLocationServiceRunning()){
            bStart = true;
        }
        else{
            bStart = false;
        }

        //main button listener
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //start the location service(Network or GPS according to strProvider)
                if (!bStart && strProvider != null) {
                    startLocationButton();
                }
                //cancel the location service
                else if (bStart && strProvider != null) {
                    stopLocationButton();
                }
            }
        });
        //radio button listener
        radButtonNetwork.setOnClickListener(new radioClickListener());
        radButtonGps.setOnClickListener(new radioClickListener());
    }

    /*
    어플의 상태를 저장한다.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(radButtonNetwork.isChecked())
            outState.putString("radButton", "network");
        else if(radButtonNetwork.isChecked())
            outState.putString("radButton", "gps");
        outState.putString("strProvider", strProvider);
        outState.putBoolean("bStart", bStart);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(null != savedInstanceState) {
            if (savedInstanceState.getString("radButton") != null) {
                if (savedInstanceState.getString("radButton").equals("network")){
                    radButtonNetwork.setChecked(true);
                    radButtonGps.setChecked(false);
                }
                else{
                        radButtonNetwork.setChecked(false);
                        radButtonGps.setChecked(true);
                }
            }
            strProvider = savedInstanceState.getString("strProvider", strProvider);
            bStart = savedInstanceState.getBoolean("bStart");
            super.onRestoreInstanceState(savedInstanceState);
        }

    }

    //change the Provider mode Network <> GPS
     private class radioClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (radButtonNetwork.isChecked()) {
                strProvider = LocationManager.NETWORK_PROVIDER;
            } else if (radButtonGps.isChecked()) {
                strProvider = LocationManager.GPS_PROVIDER;
            }
        }
    }

    public void startLocationButton(){
        bStart = true;
        stateString = strProvider + " is running...";
        textViewBelow.setText(stateString);
        iCollectInterval = Long.parseLong((editText.getText().toString()));

        Intent intentLocationService = new Intent(this, locationService.class);
        intentLocationService.putExtra("strProvider", strProvider);
        intentLocationService.putExtra("iCollectInterval", iCollectInterval);
        intentLocationService.setAction(AllConstants.START_LOCATION_FOREGROUND_ACTION);
        startService(intentLocationService);
    }

    public void stopLocationButton(){
        bStart = false;
        stateString = strProvider + " stoped";
        textViewBelow.setText(stateString);

        Intent intentLocationService = new Intent(this, locationService.class);
        intentLocationService.setAction(AllConstants.STOP_LOCATION_FOREGROUND_ACTION);
        stopService(intentLocationService);

        //locationManager.removeUpdates(locationListenerCustom);
    }

    /*
    http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
     */
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            //if ("locationService".equals(serviceInfo.service.getClassName())) {
            if(locationService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}