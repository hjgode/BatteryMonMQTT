package com.sample.batterymonmqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartJobServiceReceiver  extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyJobScheduler.scheduleJob(context);
        }

}
