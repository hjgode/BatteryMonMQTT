package com.sample.batterymonmqtt;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class MyWorkManager  {
    PeriodicWorkRequest uploadWorkRequest=null;
    /**
     * creates a new Workmanager, adds a Worker and starts it
     * @param context
     */
    public MyWorkManager(Context context) {
        //WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class).build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();

        uploadWorkRequest =
                new PeriodicWorkRequest.Builder(UploadWorker.class, 1, TimeUnit.MINUTES) //cannot be below MIN_PERIODIC_INTERVAL_MILLIS (15 Minutes)
                        .addTag("upload")
                        .setInputData(new Data.Builder()
                                .putString("mqtthost", "199.64.70.66") //"192.168.0.40")
                                .build()
                        )

                        // Constraints
                        .setConstraints(constraints)
                        //.ExistingPeriodicWorkPolicy.KEEP
                        .build();

        WorkManager.getInstance(context).enqueue(uploadWorkRequest);
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork("mqttbatpublish", ExistingPeriodicWorkPolicy.KEEP, uploadWorkRequest);
        Log.d(MainActivity.TAG, "MyWorkManger enqueued Worker: "+uploadWorkRequest.getId());
    }
}
