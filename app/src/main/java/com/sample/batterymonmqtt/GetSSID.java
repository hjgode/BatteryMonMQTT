package com.sample.batterymonmqtt;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class GetSSID {
    public static String getSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = "";
        ssid = info.getSSID();
        return ssid;
    }
}
