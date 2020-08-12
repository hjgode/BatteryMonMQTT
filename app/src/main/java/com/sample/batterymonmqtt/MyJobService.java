package com.sample.batterymonmqtt;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class MyJobService extends JobService {
    private static final String TAG = "MqttJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), LocalMqttService.class);
        getApplicationContext().startService(service);
        MyJobScheduler.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
