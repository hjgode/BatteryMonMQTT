package com.sample.batterymonmqtt;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class MyJobScheduler {
    Context _context;
    public MyJobScheduler(Context context){
        _context=context;
    }
    public void startJob() {
        JobScheduler jobScheduler = (JobScheduler) _context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(_context, MyJobService.class))
                // only add if network access is required
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(1000*60*30) //set 30 minutes interval

                .build();

        jobScheduler.schedule(jobInfo);
    }
}
