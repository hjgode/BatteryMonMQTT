package com.sample.batterymonmqtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public final static String TAG="BytteryMonMQTT";
    public static WifiReceiver wifiReceiver=new WifiReceiver();
    Context context=this;
    String[] perms=new String[]{
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPerms();
        registerReceivers(context);
        Log.v(TAG, "ssid= "+GetSSID.getSSID(context));
        BatteryInfo.getLevel(context);
        if(WifiReceiver.isConnected(context))
            Log.d(TAG, "ip="+WifiReceiver.getIP(context));
    }

    void registerReceivers(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        context.registerReceiver(wifiReceiver, intentFilter);
    }

    void requestPerms(){

        for (String s:perms
             ) {
            if( !PermissionUtils.hasPermission(this,s )) {
                PermissionUtils.requestPermissions(this, perms, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0){
            for (int i=0;i<permissions.length; i++) {
                Log.d(TAG, permissions[i] +"= "+ (grantResults[i]==0?"granted":"denied"));
            }
        }
    }
}