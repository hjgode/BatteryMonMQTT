package com.sample.batterymonmqtt;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null && intent.getAction()!=null)
            Log.d("BatteryMonMQTT", "AlarmReceiver onReceive with "+intent.getAction());
        else
            Log.d("BatteryMonMQTT", "AlarmReceiver onReceive with empty intent or action");
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if(intent.getAction()==pref.ACTION_ALARM){
            Log.d("BatteryMonMQTT", "AlarmReceiver onReceive with "+pref.ACTION_ALARM);
            ForegroundService.startService(context);
//            Runnable runnable=new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("BatteryMonMQTT", "runnable doPublish");
//                    MQTTPublisher mqttPublisher=new MQTTPublisher();
//                    mqttPublisher.doPublish(context);
//
//                }
//            };
//            runnable.run();
            //schedule a new one
            MyAlarmManger myAlarmManger=new MyAlarmManger(context);
//            MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
//            myAlarmManger.scheduleWakeup(mySharedPreferences.getMqqttInterval());
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
            int interval;
            try {
                interval = Integer.parseInt(sharedPreferences.getString(pref.PREF_MQTT_INTERVAL, "15"));
            }catch (Exception ex){
                interval=15;
            }
            myAlarmManger.scheduleWakeup(interval);

        }
    }
}
