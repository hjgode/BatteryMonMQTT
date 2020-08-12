package com.sample.batterymonmqtt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String m = intent.getStringExtra("message");
            MainActivity activity = (MainActivity)context;
            activity.updateUI(m);
    }

    public static void sendMessage(Context context, String s){
        Intent intent=new Intent();
        intent.setAction(pref.ACTION_NAME);
        intent.putExtra("message", s);
        context.sendBroadcast(intent);
    }
}
