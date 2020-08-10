package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class MyJobIntentService extends JobIntentService {
        public static final int JOB_ID = 1;
        static Context _context;

        public static void enqueueWork(Context context, Intent work) {
            _context=context;
            enqueueWork(_context, MyJobIntentService.class, JOB_ID, work);
        }

        @Override
        protected void onHandleWork(@NonNull Intent intent) {
            // your code
            MySharedPreferences mySharedPreferences=new MySharedPreferences(_context);
            String host=mySharedPreferences.getHost();
            MQTTPublisher mqttPublisher=new MQTTPublisher(host);
            mqttPublisher.doPublish(_context, BatteryInfo.getBattInfo(_context), host);
        }


}
