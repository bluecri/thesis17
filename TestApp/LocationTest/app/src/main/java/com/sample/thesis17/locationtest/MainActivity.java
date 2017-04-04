package com.sample.thesis17.locationtest;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
    locationListener locationListenerCustom;
    LocationManager locationManager;

    boolean bStart = false;
    long minTime = 0;
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

        //main button listener
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                long lTempTime = 10000;
                //start the location service(Network or GPS according to strProvider)
                if (!bStart && strProvider != null) {
                    bStart = true;
                    stateString = strProvider + " is running...";
                    textViewBelow.setText(stateString);
                    lTempTime = Long.parseLong((editText.getText().toString()));
                    startLocationService(lTempTime);
                }
                //cancel the location service
                else if (bStart && strProvider != null) {
                    bStart = false;
                    stateString = strProvider + " stoped";
                    textViewBelow.setText(stateString);
                    locationManager.removeUpdates(locationListenerCustom);
                }
            }
        });
        //radio button listener
        radButtonNetwork.setOnClickListener(new radioClickListener());
        radButtonGps.setOnClickListener(new radioClickListener());
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
    //
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

                textView.setText("위/경도 : " + latitude + ", " + longitude);
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

            textView.setText("location changed... : " + latitude + ", " + longitude);
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