package com.sample.batterymonmqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.app.Service.START_NOT_STICKY;

//needs <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    final Context context=this;
    final int SERVICE_ID=23;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_stat_batt)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(SERVICE_ID, notification);
        //do heavy work on a background thread
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
//                MQTTPublisher mqttPublisher=new MQTTPublisher();
//                mqttPublisher.doPublish(context);
                MqttPublisherHiveMQ mqttPublisherHiveMQ=new MqttPublisherHiveMQ(context);
                mqttPublisherHiveMQ.doPublish();
            }
        };
        runnable.run();
        stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static void startService(Context ctx) {
        Intent serviceIntent = new Intent(ctx, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "MQTT battery publisher");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(serviceIntent);
        }else {
            ContextCompat.startForegroundService(ctx, serviceIntent);
        }
    }
    public void stopService(Context ctx) {
        Intent serviceIntent = new Intent(ctx, ForegroundService.class);
        stopService(serviceIntent);
    }
}