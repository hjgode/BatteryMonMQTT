package com.sample.batterymonmqtt;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import static android.content.Context.BATTERY_SERVICE;

public class BatteryInfo {
    public static int getLevel(Context context){
        int l=0;boolean status=false;
        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            status = bm.isCharging();
            l=percentage;
        }
        Log.d(MainActivity.TAG, "getLevel="+l+ (status?" charging":" discharging"));
        return l;
    }
}
