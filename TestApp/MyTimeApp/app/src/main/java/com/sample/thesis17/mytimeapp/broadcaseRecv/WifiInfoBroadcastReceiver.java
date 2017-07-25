package com.sample.thesis17.mytimeapp.broadcaseRecv;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telecom.ConnectionService;
import android.util.Log;

import com.sample.thesis17.mytimeapp.locationS.AllConstants;
import com.sample.thesis17.mytimeapp.locationS.locationService;

public class WifiInfoBroadcastReceiver extends BroadcastReceiver {

    public static final String STR_WIFI_CONN = "WIFI";
    public static final String STR_DATA_CONN = "MOBILE";
    public static final String STR_NO_CONN = "NOCONN";

    Intent intentLocationService = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        /*if(intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")){
            NetworkInfo nInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(nInfo != null && nInfo.isConnected()){
                Log.d("WifiInfoBroad", "wifi conn");
            }
            else{
                Log.d("WifiInfoBroad", "wifi not conn");
            }
            //throw new UnsupportedOperationException("Not yet implemented");
        }*/
        if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = connectivityManager.getActiveNetworkInfo();
            if(nInfo != null){
                if((nInfo.getType() == ConnectivityManager.TYPE_WIFI) && nInfo.isConnectedOrConnecting()){
                    Log.d("WifiInfoBroad", "wifi");
                    intentLocationService = new Intent(context, locationService.class);
                    intentLocationService.putExtra("strNetworkStatus", "WIFI");
                    intentLocationService.setAction(AllConstants.NETWORK_STATUS_CHANGED_ACTION);
                    context.startService(intentLocationService);
                }
                else if((nInfo.getType() == ConnectivityManager.TYPE_MOBILE) && nInfo.isConnectedOrConnecting()){
                    Log.d("WifiInfoBroad", "mobile");
                    intentLocationService = new Intent(context, locationService.class);
                    intentLocationService.putExtra("strNetworkStatus", "MOBILE");
                    intentLocationService.setAction(AllConstants.NETWORK_STATUS_CHANGED_ACTION);
                    context.startService(intentLocationService);
                }
                else{
                    Log.d("WifiInfoBroad", "???");
                }
            }
            else{
                //no connection
                Log.d("WifiInfoBroad", "no conn");
                intentLocationService = new Intent(context, locationService.class);
                intentLocationService.putExtra("strNetworkStatus", "NOCONN");
                intentLocationService.setAction(AllConstants.NETWORK_STATUS_CHANGED_ACTION);
                context.startService(intentLocationService);
            }
            //NetworkInfo dataInfo = connectivityManager.getActiveNetworkInfo()



        }

    }


}