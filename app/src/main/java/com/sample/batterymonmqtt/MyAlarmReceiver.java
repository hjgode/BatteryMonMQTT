package com.sample.batterymonmqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if(intent.getAction()==pref.ACTION_ALARM){
            MQTTPublisher mqttPublisher=new MQTTPublisher();
            mqttPublisher.doPublish(context);
        }
    }
}
