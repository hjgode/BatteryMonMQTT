package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MySharedPreferences {
    Context _context=null;
    SharedPreferences sharedPreferences=null;
    String mqtt_host="192.168.0.40";
    String mqtt_port="1883";
    String mqtt_interval="15";
    String mqtt_topic ="geraet1";

    int mqttInterval=15;
    int mqttport=1883;
    boolean mqtt_enabled=true;

    public MySharedPreferences(Context context){
        _context=context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //read all values or defaults
        mqtt_host=getHost();
        mqttInterval=getMqqttInterval();
        mqtt_port=getPort();
        mqtt_topic=getTopic();
        if(mqtt_topic.equals("geraet1")) {
            mqtt_topic = Build.DEVICE;
            saveTopic(mqtt_topic);
        }

        saveAll();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        Log.d(TAG, "onSharedPreferenceChanged: "+key);
        // do stuff
        if (key == pref.PREF_MQTT_HOST)
            mqtt_host = getHost();
        else if (key == pref.PREF_MQTT_INTERVAL)
            mqtt_interval = getMqqttInterval() + "";
        else if (key == pref.PREF_MQTT_PORT)
            mqtt_interval = getPort() + "";
        else if (key == pref.PREF_MQTT_TOPIC)
            mqtt_interval = getTopic() ;
        else  if (key == pref.PREF_MQTT_ENABLED)
            mqtt_enabled = getEnabled() ;

        MainActivity mainActivity=MainActivity.getInstance();
        mainActivity.startWorker(_context);
    }

    private boolean getEnabled() {
        boolean h=sharedPreferences.getBoolean(pref.PREF_MQTT_ENABLED, mqtt_enabled);
        return h;
    }

    @Override
    public String toString(){
        String s="mqtt_enabled="+mqtt_enabled+ ", mqtt_host="+mqtt_host+", mqtt_port="+mqtt_port+", mqtt_interval="+mqtt_interval+", topic="+mqtt_topic;
        return  s;
    }
    public void saveAll(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(pref.PREF_MQTT_HOST, mqtt_host);
        editor.putString(pref.PREF_MQTT_PORT, mqtt_port);
        editor.putString(pref.PREF_MQTT_INTERVAL, mqtt_interval);
        editor.putString(pref.PREF_MQTT_TOPIC, mqtt_topic);
        editor.putBoolean(pref.PREF_MQTT_ENABLED, mqtt_enabled);
        editor.apply();
    }
    public void saveHost(String h){
        mqtt_host=h;
        sharedPreferences.edit().putString(pref.PREF_MQTT_HOST, mqtt_host);
        sharedPreferences.edit().apply();
    }

    public void savePort(String h){
        mqtt_host=h;
        sharedPreferences.edit().putString(pref.PREF_MQTT_PORT, mqtt_port);
        sharedPreferences.edit().apply();
    }

    public void saveInterval(int v){
        mqtt_interval=v+"";
        sharedPreferences.edit().putString(pref.PREF_MQTT_INTERVAL, mqtt_interval);
        sharedPreferences.edit().apply();
    }

    public void saveTopic(String v){
        mqtt_topic=v;
        sharedPreferences.edit().putString(pref.PREF_MQTT_TOPIC, mqtt_topic);
        sharedPreferences.edit().apply();
    }

    public void saveEnabled(boolean v){
        mqtt_enabled=v;
        sharedPreferences.edit().putBoolean(pref.PREF_MQTT_TOPIC, mqtt_enabled);
        sharedPreferences.edit().apply();
    }

    public String getTopic(){
        String h=sharedPreferences.getString(pref.PREF_MQTT_TOPIC, mqtt_topic);
        return h;
    }

    public String getHost(){
        String h=sharedPreferences.getString(pref.PREF_MQTT_HOST, "192.168.0.40");
        return h;
    }

    public String getPort(){
        String h=sharedPreferences.getString(pref.PREF_MQTT_PORT, "1883");
        try {
            mqttport = Integer.parseInt(h);
        }catch (Exception ex){
            Log.d(TAG, "SharedPreferneces, mqtt_port not a number?: "+ex.getMessage());
        }
        return h;
    }

    public int getMqqttInterval(){
        int v=30;
        mqtt_interval=sharedPreferences.getString(pref.PREF_MQTT_INTERVAL, "30");
        try{
            v=Integer.parseInt(mqtt_interval);
        }catch(Exception ex){
            v=30;
            Log.d(TAG, "SharedPreferneces, mqtt_interval not a number?: "+ex.getMessage());
        }
        return v;
    }
}
