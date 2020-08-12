package com.sample.batterymonmqtt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class LocalMqttService extends Service {
    private final IBinder mBinder = new MyBinder();
    private int counter = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "LocalMqttService::onStartCommand...");
        publishBattInfo();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "LocalMqttService::onBind...");
        publishBattInfo();
        return mBinder;
    }

    public class MyBinder extends Binder {
        LocalMqttService getService() {
            return LocalMqttService.this;
        }
    }

    void publishBattInfo(){
        Log.d(TAG, "LocalMqttService::publishBattInfo...");
        BatteryInfo.BattInfo battInfo= BatteryInfo.getBattInfo(this);
        MQTTPublisher mqttPublisher=new MQTTPublisher();
        MySharedPreferences mySharedPreferences=new MySharedPreferences(this);
        mqttPublisher.doPublish(this, battInfo, mySharedPreferences.mqtt_host, mySharedPreferences.getPort());
    }
}
