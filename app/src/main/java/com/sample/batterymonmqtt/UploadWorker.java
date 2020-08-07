package com.sample.batterymonmqtt;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {
    Context _context;
    MQTTPublisher mqttPublisher=null;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        _context=context;
    }

    @Override
    public ListenableWorker.Result doWork() {
        String mqtthost = getInputData().getString("mqtthost");
        // Do the work here--in this case, upload the images.
        //uploadImages();
        PublishBatteryLevel(_context, mqtthost);
        // Indicate whether the work finished successfully with the Result

        return ListenableWorker.Result.success();
    }

    public int PublishBatteryLevel(Context context, String host){
        int res= 0;
        BatteryInfo.BattInfo batinfo = BatteryInfo.getBattInfo(context);
        if(mqttPublisher==null) {
            mqttPublisher = new MQTTPublisher(host);
        }
        mqttPublisher.doPublish(context, batinfo, host);

        Log.d(MainActivity.TAG,"published battery ="+ batinfo.ToString());
        return res;
    }
}