package com.sample.batterymonmqtt;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MyJobService extends JobService {
    private static final String TAG = "MqttJobService";
    Thread jobThread=null;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob...");
//        publishBattInfo(this);
        Context context=this;
        if(jobThread!=null)
            jobThread.interrupt();
        jobThread= new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "LocalMqttService::publishBattInfo...");
                BatteryInfo.BattInfo battInfo= BatteryInfo.getBattInfo(context);
                MQTTPublisher mqttPublisher=new MQTTPublisher();
                MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
                mqttPublisher.doPublish(context, battInfo, mySharedPreferences.mqtt_host, mySharedPreferences.getPort(),
                        mySharedPreferences.getTopic());
            }
        });
        jobThread.start(); //call when onStartJob returns true (runs in background) to signal job finished!
        jobFinished(params, false);

        //        Intent service = new Intent(getApplicationContext(), LocalMqttService.class);
//        getApplicationContext().startService(service);
//        MyJobScheduler.scheduleJob(getApplicationContext()); // reschedule the job

        //add another job?
        //MyJobScheduler.scheduleJob(this);
        Log.d(TAG, "onStartJob ended");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob...");
        if(jobThread!=null)
            jobThread.interrupt();
        jobThread=null;
        Log.d(TAG, "onStopJob ended");
        return true;
    }

    void publishBattInfo(Context context){
    }

}
