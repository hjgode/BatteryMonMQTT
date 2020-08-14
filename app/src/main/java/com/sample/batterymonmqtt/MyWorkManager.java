package com.sample.batterymonmqtt;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MyWorkManager  {
    PeriodicWorkRequest uploadWorkRequest=null;
    final String MY_WORK_TAG="MQTT_BAT_WORKER";
    Context _context;
    Constraints constraints;
    /**
     * creates a new Workmanager, adds a Worker and starts it
     * @param context
     */
    public MyWorkManager(Context context) {
        //WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class).build();
        _context=context;
        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build();
    }

    public void clearAllRequests(){
        WorkManager.getInstance(_context).cancelAllWorkByTag(MY_WORK_TAG);
    }

    public void stopRequests(){
        if(uploadWorkRequest!=null) {
            WorkManager.getInstance(_context).cancelAllWorkByTag(MY_WORK_TAG);
        }
    }

    public void startRequests(){
        MySharedPreferences mySharedPreferences=new MySharedPreferences(_context);
        String host=mySharedPreferences.getHost();
        String port=mySharedPreferences.getPort();
        String ints=mySharedPreferences.getMqqttInterval()+"";
        int intv=mySharedPreferences.getMqqttInterval();

        stopRequests();

        UpdateReceiver.sendMessage(_context, "startRequests with "+host+":"+port+", interval="+ints);

        Log.d(TAG, "uploadRequest starting with interval="+intv+", host="+host);
        uploadWorkRequest =
                new PeriodicWorkRequest.Builder(UploadWorker.class, intv, TimeUnit.MINUTES, intv/2, TimeUnit.MINUTES) //cannot be below MIN_PERIODIC_INTERVAL_MILLIS (15 Minutes)
                        .addTag(MY_WORK_TAG)

                        // Constraints
                        .setConstraints(constraints)
                        //.ExistingPeriodicWorkPolicy.KEEP
                        .build();

        WorkManager.getInstance(_context).enqueueUniquePeriodicWork(MY_WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest);
        Log.d(TAG, "MyWorkManger enqueued Worker: "+uploadWorkRequest.getId());
                WorkManager.getInstance(_context).getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                .observeForever(new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo != null ){
                            Log.d(TAG, "WorkManager state="+workInfo.getState());
                            if(workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                Log.i(TAG, "Work finished!");
                            }
                        }
                    }
                });

    }
}
