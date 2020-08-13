package com.sample.batterymonmqtt;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MyJobScheduler {
    final int MY_JOB_ID=1883;

    public MyJobScheduler(Context context){

    }

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context) {
        Log.d(TAG, "scheduleJob...");
        MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
        long max=mySharedPreferences.mqttInterval;

        ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setPeriodic(max *60*1000, max/2 *60*1000);
//        builder.setMinimumLatency((max/2) * 60 * 1000); // wait at least seconds, min 15 minutes
//        builder.setOverrideDeadline(max * 60 * 1000); // maximum delay seconds, max 30 minutes
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false); //don't care
            builder.setRequiresStorageNotLow(false); //don't care
        }
        builder.setPersisted(false); //we use a BOOT receiver to schedule
        builder.setRequiresCharging(false); //don't care
        builder.setRequiresDeviceIdle(false); //don't care
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        jobScheduler.cancelAll();

        int res = jobScheduler.schedule(builder.build());   //1=success, 0=failure
        Log.d(TAG, "jobSchedule result="+res);
    }
}
