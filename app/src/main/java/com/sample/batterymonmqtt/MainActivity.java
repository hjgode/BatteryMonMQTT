package com.sample.batterymonmqtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String TAG="BatteryMonMQTT";
    public static WifiReceiver wifiReceiver=new WifiReceiver();
    Context context=this;
//    MyWorkManager myWorkManager=null;
    private static MainActivity instance;
    TextView logtext;
    UpdateReceiver updateReceiver=new UpdateReceiver();
    MQTTPublisher mqttPublisher;
     MyAlarmReceiver myAlarmReceiver = new MyAlarmReceiver();

    String[] perms=new String[]{
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.READ_PHONE_STATE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logtext=findViewById(R.id.logtext);
        logtext.setMovementMethod(new ScrollingMovementMethod());

        instance=this;
        requestPerms();

        registerUpdateReceiver(context);

//        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
//        Log.d(TAG,"PREFS: "+sharedPreferences.getAll().toString());

        MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
        Log.d(TAG,"PREFS: "+mySharedPreferences.toString());

        UpdateReceiver.sendMessage(context, mySharedPreferences.toString());
//        org.apache.log4j.BasicConfigurator.configure();

        registerWifiReceiver(context);

        Log.v(TAG, "ssid= "+GetSSID.getSSID(context));
        BatteryInfo.getBattInfo(context);
        if(WifiReceiver.isConnected(context))
            Log.d(TAG, "ip="+WifiReceiver.getIP(context));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                mqttPublisher=new MQTTPublisher();
//                mqttPublisher.doPublish(context);
                MqttPublisherHiveMQ mqttPublisherHiveMQ=new MqttPublisherHiveMQ(context);
                mqttPublisherHiveMQ.doPublish();
            }
        };
        runnable.run();

//        registerAlarmReceiver();
        startWorker(context);
//        setAlarmPendingIntent();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerWifiReceiver(context);
        registerUpdateReceiver(context);
        registerAlarmReceiver();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(updateReceiver);
        unregisterAlarmReceiver();
    }

    private void setAlarmPendingIntent() {
        Intent intent = new Intent(this, ForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "publish after every 15 minutes interval", Toast.LENGTH_LONG).show();
    }


    public boolean isAlarmBroadcastRegistered(Context context, String action, Class clazz) {
        Intent intent = new Intent(action);
        intent.setClass(context, clazz);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    void unregisterAlarmReceiver(){
        context.unregisterReceiver(myAlarmReceiver);
    }
    void registerAlarmReceiver(){
        final IntentFilter intentFilter = new IntentFilter(pref.ACTION_ALARM);
        context.registerReceiver(myAlarmReceiver, intentFilter);

/*
        //fire this to launch the receiver
        Intent intent = new Intent(pref.ACTION_ALARM);
        intent.setClass(this, MyAlarmReceiver.class);
*/
    }

    public void updateUI(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(s.equals("CLEAR"))
                    logtext.setText("");
                else
                    logtext.append(s+"\n");
            }
        });
    }
    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * Inflates the menu, and adds items to the action bar if it is present.
     *
     * @param menu Menu to inflate.
     * @return Returns true if the menu inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handles app bar item clicks.
     *
     * @param item Item clicked.
     * @return True if one of the defined items was clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menuitem:
                Intent settingsIntent = new Intent(this,
                        SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                // Do nothing
        }

        return super.onOptionsItemSelected(item);
    }

    public void startWorker(final Context context){
        Log.d(TAG, "startWorker()");
        if(mqttPublisher!=null)
            mqttPublisher.stopPublish();

        UpdateReceiver.sendMessage(context, "starting Schedule...");

        //MyJobScheduler.scheduleJob(context);

        MyAlarmManger myAlarmManger=new MyAlarmManger(context);
        MySharedPreferences mySharedPreferences=new MySharedPreferences(context);
        myAlarmManger.scheduleWakeup(mySharedPreferences.getMqqttInterval());

        //####################################
//        if(myWorkManager==null)
//            myWorkManager=new MyWorkManager(context);
//        myWorkManager.clearAllRequests();
//        myWorkManager.startRequests();
        //####################################
//        WorkManager.getInstance(context).getWorkInfoByIdLiveData(myWorkManager.uploadWorkRequest.getId())
//                .observe(this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(@Nullable WorkInfo workInfo) {
//                        if (workInfo != null ){
//                            Log.d(TAG, "WorkManager state="+workInfo.getState());
////                            if(workInfo.getState() == WorkInfo.State.SUCCEEDED) {
////                                displayMessage("Work finished!");
////                            }
//                        }
//                    }
//                });
    }

    void registerUpdateReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(pref.ACTION_NAME);
        context.registerReceiver(updateReceiver, intentFilter);
    }

    void registerWifiReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        context.registerReceiver(wifiReceiver, intentFilter);
    }

    void requestPerms(){

        for (String s:perms
             ) {
            if( !PermissionUtils.hasPermission(this,s )) {
                PermissionUtils.requestPermissions(this, perms, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0){
            for (int i=0;i<permissions.length; i++) {
                Log.d(TAG, permissions[i] +"= "+ (grantResults[i]==0?"granted":"denied"));
            }
        }
    }
}