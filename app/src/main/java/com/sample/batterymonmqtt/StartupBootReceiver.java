package com.sample.batterymonmqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

/***
 * this is called on evry boot
 * BUT, the app needs to be run one time manually by the user
 * before this will work
 */
public class StartupBootReceiver extends BroadcastReceiver {
    final String TAG ="StartupBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "StartUpBootReceiver BOOT_COMPLETED");
            //...
        }
    }

}
