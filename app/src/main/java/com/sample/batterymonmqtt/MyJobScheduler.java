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
    final static int JOB_ID=1883;

    public MyJobScheduler(Context context){

    }

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context) {
        Log.d(TAG, "scheduleJob...");
        MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
        long interval=mySharedPreferences.mqttInterval;
        //TODO: remove after test
        //interval=15;
        ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent); //do not use id=0 here, wil cancel jobs with same ID
        //infos: https://debruyn.dev/2018/tips-for-developing-android-jobscheduler-jobs/
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            builder.setPeriodic(interval*60*1000);
        else
            builder.setPeriodic(interval *60*1000, 5 *60*1000);
//        builder.setMinimumLatency((max/2) * 60 * 1000); // wait at least seconds, min 15 minutes
//        builder.setOverrideDeadline(max * 60 * 1000); // maximum delay seconds, max 30 minutes
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false); //don't care
            builder.setRequiresStorageNotLow(false); //don't care
        }

        builder.setPersisted(true); //we use a BOOT receiver to schedule
        builder.setRequiresCharging(false); //don't care
        builder.setRequiresDeviceIdle(false); //don't care

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        JobInfo pendingJob = jobScheduler.getPendingJob(JOB_ID);
        if (pendingJob == null)
        {
//        jobScheduler.cancelAll();
            int res = jobScheduler.schedule(builder.build());   //1=success, 0=failure
            Log.d(TAG, "new jobSchedule result="+res);
        }
        else {
            // maybe verify the job settings here
            Log.d(TAG, "already pending jobs: "+pendingJob.getIntervalMillis()*1000+"seconds interval");
        }
        Log.d(TAG, "scheduleJob ended");
    }
}
