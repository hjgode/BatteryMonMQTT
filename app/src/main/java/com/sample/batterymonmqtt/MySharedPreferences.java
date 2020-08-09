package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPreferences {
    Context _context=null;
    SharedPreferences sharedPreferences=null;
    String mqtt_host="192.168.0.40";
    String mqtt_interval="30";
    int mqttInterval=30;
    private String sharedPrefFile = "com.sample.batterymonmqtt";

    public MySharedPreferences(Context context){
        _context=context;
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences=context.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        mqtt_host=getHost();
        mqttInterval=getMqqttInterval();
    }

    public void saveHost(String h){
        mqtt_host=h;
        sharedPreferences.edit().putString("mqtt_host", mqtt_host);
    }

    public void saveInterval(int v){
        mqtt_interval=v+"";
        sharedPreferences.edit().putString("mqtt_interval", mqtt_interval);
    }

    public String getHost(){
        String h=sharedPreferences.getString("mqtt_host", "192.168.0.40");
        return h;
    }

    public int getMqqttInterval(){
        int v=30;
        mqtt_interval=sharedPreferences.getString("mqtt_interval", "30");
        try{
            v=Integer.parseInt(mqtt_interval);
        }catch(Exception ex){
            v=30;
            Log.d(MainActivity.TAG, "SharedPreferneces, mqtt_interval not a number?: "+ex.getMessage());
        }
        return v;
    }
    public String getMqttHost(){
        return mqtt_host;
    }
}
