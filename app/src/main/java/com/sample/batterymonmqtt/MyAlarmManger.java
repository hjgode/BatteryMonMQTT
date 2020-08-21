package com.sample.batterymonmqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MyAlarmManger {
    Context context=null;
    final int requestId=815;
    PendingIntent alarmIntent=null;
    Intent intentAlarmReceiver = null;

    static AlarmManager alarmManager=null;

    public MyAlarmManger(Context _context){
        context= _context;
        if(alarmManager==null)
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intentAlarmReceiver=new Intent(context, MyAlarmReceiver.class);
        intentAlarmReceiver.setAction(pref.ACTION_ALARM); // https://visdap.blogspot.com/2019/04/android-notifications-triggered-by.html
        intentAlarmReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, 0);
    }

    void scheduleWakeup(int interval){
        //this needs to be rescheduled!
//        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, 1*60*1000, alarmIntent);
        Log.d(TAG, "scheduleWakeup:AlarmManager canceling Alarm and PendingIntent");
        alarmManager.cancel(alarmIntent);
        alarmIntent.cancel();
        registerAlarm(context, interval, alarmIntent);

//        //long minutesFromNow = System.currentTimeMillis() + 60 * 1000*interval; //use, if you trust the clock will not change
//        //works for time since boot only:
//        long minutesFromNow =  SystemClock.elapsedRealtime() + 60 * 1000 * interval;
//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, minutesFromNow, alarmIntent);
//        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, interval*60*1000, alarmIntent); //will set the alarm relative to the clock
        Log.d(TAG, "scheduleWakeup:AlarmManager setAlarm to start in " + interval+" minutes");
/*        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                interval*60*1000
                //AlarmManager.INTERVAL_HALF_HOUR
                , alarmIntent);
*/
    }

    public void cancelAlarm()
    {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, requestId, intentAlarmReceiver,
                        PendingIntent.FLAG_NO_CREATE); //should be the same as alarmIntent
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            //alarmManager.cancel(alarmIntent);
        }

    }

    /**
     * schedule an alarm
     * see https://visdap.blogspot.com/2019/04/android-notifications-triggered-by.html
     * @param context
     * the context
     * @param interval
     * when to launch the AlarmReceiver in minutes
     * @param intent
     * existing PendingIntent to be used
     */
    public static void registerAlarm(Context context, long interval, PendingIntent intent){
        final int FIVE_MINUTES_IN_MILLI = 300000;
        final int THIRTY_SECOND_IN_MILLI = 30000;
        long launchTime = System.currentTimeMillis() + (60*1000 * interval);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent i = new Intent(context, MyAlarmReceiver.class);
        PendingIntent pi = intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, launchTime, pi);
        else
            am.setExact(AlarmManager.RTC_WAKEUP, launchTime, pi);
        Log.d(TAG, "registerAlarm for timestamp "+launchTime);
    }

    public static void registerAlarm(Context context){
        final int FIVE_MINUTES_IN_MILLI = 300000;
        final int THIRTY_SECOND_IN_MILLI = 30000;
        long launchTime = System.currentTimeMillis() + FIVE_MINUTES_IN_MILLI;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MyAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, launchTime, pi);
        else am.setExact(AlarmManager.RTC_WAKEUP, launchTime, pi);
        Log.d(TAG,"timestamp "+launchTime);
    }
}
