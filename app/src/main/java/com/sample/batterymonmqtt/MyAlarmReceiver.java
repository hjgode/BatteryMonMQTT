package com.sample.batterymonmqtt;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        }
    }
}
