package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;
import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MySharedPreferences {
    Context _context=null;
    SharedPreferences sharedPreferences=null;
    String mqtt_host="192.168.0.40";
    String mqtt_interval="30";
    int mqttInterval=30;
    private String sharedPrefFile = "com.sample.batterymonmqtt";

    public MySharedPreferences(Context context){
        _context=context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mqtt_host=getHost();
        mqttInterval=getMqqttInterval();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        Log.d(TAG, "onSharedPreferenceChanged: "+key);
        // do stuff
        if (key == "mqtt_host")
            mqtt_host = getHost();
        else if (key == "mqtt_interval")
            mqtt_interval = getMqqttInterval() + "";
        MainActivity mainActivity=MainActivity.getInstance();
        mainActivity.startWorker(_context);
    }

    public void saveHost(String h){
        mqtt_host=h;
        sharedPreferences.edit().putString("mqtt_host", mqtt_host);
        sharedPreferences.edit().apply();
    }

    public void saveInterval(int v){
        mqtt_interval=v+"";
        sharedPreferences.edit().putString("mqtt_interval", mqtt_interval);
        sharedPreferences.edit().apply();
    }

    public String getHost(){
        String h=sharedPreferences.getString("mqtt_host", "### 192.168.0.40");
        return h;
    }

    public int getMqqttInterval(){
        int v=30;
        mqtt_interval=sharedPreferences.getString("mqtt_interval", "30");
        try{
            v=Integer.parseInt(mqtt_interval);
        }catch(Exception ex){
            v=30;
            Log.d(TAG, "SharedPreferneces, mqtt_interval not a number?: "+ex.getMessage());
        }
        return v;
    }
}
