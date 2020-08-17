package com.sample.batterymonmqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.sample.batterymonmqtt.MainActivity.TAG;

public class UploadWorker extends Worker {
    Context _context;
    MQTTPublisher mqttPublisher=null;
    private NotificationManager notificationManager;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        _context=context;
        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public ListenableWorker.Result doWork() {

        // Do the work here--in this case, upload the images.
        //uploadImages();
        setForegroundAsync(createForegroundInfo("MQTT BatteryMon running"));
        try {
            PublishBatteryLevel(_context);
        }catch (Exception ex){
            Log.e(TAG, "ListenableWorker:Exception with PublishBatteryLevel: "+ex.getMessage());
            return ListenableWorker.Result.failure();
        }
        // Indicate whether the work finished successfully with the Result

        return ListenableWorker.Result.success();
    }

    public int PublishBatteryLevel(Context context){
        int res= 0;
        BatteryInfo.BattInfo batinfo = BatteryInfo.getBattInfo(context);
        MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
        String host=mySharedPreferences.getHost();
        String port=mySharedPreferences.getPort();

        Log.d(TAG,"UploadWorker::PublishBatteryLevel "+ batinfo.ToString()+" to "+ host);
        if(mqttPublisher==null) {
            mqttPublisher = new MQTTPublisher();
        }
        mqttPublisher.doPublish(context, batinfo, host, port, new MySharedPreferences(context).getTopic());


        return res;
    }

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();
        String id = "MQTT_CHANNEL_ID";
        int Id=1022;
        String title = "MQTT Battery Monitor";
        String cancel = "stop Monitor";
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context).createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id, title, "MQTT battery monitor");
        }

        Notification notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_stat_batt)
                .setOngoing(true)
                .setChannelId(id)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();

        return new ForegroundInfo(Id, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel(String CHANNEL_ID, String name, String description) {
        // Create a Notification channel
//        CharSequence name = getString(R.string.channel_name);
//        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Don't see these lines in your code...
        NotificationManager notificationManager = _context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}