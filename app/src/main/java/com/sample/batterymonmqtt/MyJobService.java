package com.sample.batterymonmqtt;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.widget.Toast;

public class MyJobService extends JobService {
    Context _context=this;

    public MyJobService(){
        super();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // runs on the main thread, so this Toast will appear
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        // perform work here, i.e. network calls asynchronously
        // your code
        MySharedPreferences mySharedPreferences=new MySharedPreferences(_context);
        String host=mySharedPreferences.getHost();
        MQTTPublisher mqttPublisher=new MQTTPublisher(host);
        mqttPublisher.doPublish(_context, BatteryInfo.getBattInfo(_context), host);
        // returning false means the work has been done, return true if the job is being run asynchronously
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // if the job is prematurely cancelled, do cleanup work here

        // return true to restart the job
        return false;
    }
}
