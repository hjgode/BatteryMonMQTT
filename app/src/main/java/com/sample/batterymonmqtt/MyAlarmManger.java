package com.sample.batterymonmqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

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
        intentAlarmReceiver.setAction(pref.ACTION_ALARM);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intentAlarmReceiver, 0);
    }

    void scheduleWakeup(int interval){
        //this needs to be rescheduled!
//        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, 1*60*1000, alarmIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, interval*60*1000, alarmIntent);
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
}
