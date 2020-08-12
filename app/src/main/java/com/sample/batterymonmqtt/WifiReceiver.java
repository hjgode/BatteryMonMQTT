package com.sample.batterymonmqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import static android.content.Context.WIFI_SERVICE;

public class WifiReceiver extends BroadcastReceiver {
    final String TAG="WifiReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                //do stuff
                Log.v(TAG, GetSSID.getSSID(context)+" connected"); //needs Fine_Location and GPS ON!
                if(isConnected(context)){
                    Log.d(TAG, "ip="+getIP(context));
                }
            } else {
                // wifi connection was lost
                Log.v(TAG, "network disconnected");
            }
        }
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isMetered = cm.isActiveNetworkMetered();

        return isConnected;
    }

    public static String getIP(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ipAddress;
    }

    static String macaddress="n/a";
    public static String getMac(Context context){
        String address=macaddress;
        if(macaddress=="n/a") {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            address = info.getMacAddress();
            macaddress = address;
        }
        return address;
    }
}
