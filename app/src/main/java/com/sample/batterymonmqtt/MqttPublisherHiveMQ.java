package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.hivemq.client.internal.logging.InternalLoggerFactory;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.util.UUID;

public class MqttPublisherHiveMQ {
    Context _context;
    final String TAG="MqttPublisherHiveMQ";
    static PowerManager.WakeLock mWakeLock;

    MqttPublisherHiveMQ(Context context){
        _context=context;
    }

    public void doPublish(){
        Log.d(TAG, "doPublish()...");
        if(MyNetwork.getNetworkType(_context)!= MyNetwork.NetworkType.UNMETERED){
            Log.d(TAG, "Network not unmetered. No publish!");
            return;
        }
        doWakeLock(_context);
//        MySharedPreferences mySharedPreferences=new MySharedPreferences(_context);
//        UpdateReceiver.sendMessage(_context, "doPublish(): prefs="+mySharedPreferences.toString());
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(_context);
        UpdateReceiver.sendMessage(_context, "doPublish(): prefs="+MySharedPreferences.dumpPrefs(sharedPreferences));

        Log.d(TAG, "doPublish()...build client...");
        Mqtt3AsyncClient client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                //.serverHost(mySharedPreferences.getHost())
                .serverHost(sharedPreferences.getString(pref.PREF_MQTT_HOST, _context.getResources().getString(R.string.mqtt_default_host)))
                //.serverPort(mySharedPreferences.getPortInt())
                .serverPort(Integer.parseInt(sharedPreferences.getString(pref.PREF_MQTT_PORT, _context.getResources().getString(R.string.mqtt_default_port))))
                .buildAsync();
        Log.d(TAG, "doPublish()...client.connect()...");
        client.connect()
                .whenComplete((connAck, throwable) -> {
                    Log.d(TAG, "doPublish()...client,connect()...whenCompleted...");
                    if (throwable != null) {
                        // Handle connection failure
                        UpdateReceiver.sendMessage(_context, "connect failed "+throwable.getMessage());
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...ERROR: "+throwable.getMessage());
                    } else {
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success");
                        // Setup subscribes or start publishing
                        String payloadS=MyJSON.getJSON(BatteryInfo.getBattInfo(_context));
                        byte[] payload=payloadS.getBytes();
                        String topic="android/batteries/"+sharedPreferences.getString(pref.PREF_MQTT_TOPIC, _context.getResources().getString(R.string.mqtt_default_topic));
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...");
                        Log.d(TAG, "topic="+topic);
                        client.publishWith()
                                //.topic("android/batteries/" + mySharedPreferences.getTopic())
                                .topic(topic)
                                .payload(payload)
                                .qos(MqttQos.AT_MOST_ONCE)
                                .retain(true)
                                .send()
                                .whenComplete((mqtt3Publish, throwable1) -> {
                                    if (throwable1 != null) {
                                        // Handle failure to publish
                                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...ERROR: "+throwable1.getMessage());
                                        UpdateReceiver.sendMessage(_context, "doPublish failed "+throwable1.getMessage());
                                    } else {
                                        // Handle successful publish, e.g. logging or incrementing a metric
                                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...completed");
                                        //UpdateReceiver.sendMessage(_context, "doPublish OK: "+ mySharedPreferences.toString() +"\n"+ payloadS);
                                        UpdateReceiver.sendMessage(_context, "doPublish OK: "+ MySharedPreferences.dumpPrefs(sharedPreferences) +"\n"+ payloadS);

                                        //DISCONNECT
                                        Log.d(TAG, "doPublish()...disconnect()...");
                                        client.disconnect()
                                                .whenComplete((aVoid, throwable2) -> {
                                                    if(throwable2!=null){
                                                        Log.d(TAG, "doPublish()...disconnect()...ERROR: "+throwable2.getMessage());
                                                        UpdateReceiver.sendMessage(_context, "disconnect() failed "+throwable2.getMessage());
                                                    }else{
                                                        Log.d(TAG, "doPublish()...disconnect()...success");
                                                        UpdateReceiver.sendMessage(_context, "disconnect() OK");
                                                    }
                                                });

                                    }//publish OK
                                });

                    }//connect OK
                });

        Log.d(TAG, "doPublish()...END");
    }

    void doWakeLock(Context context){
        final long SECONDS=10L;
        Log.d(TAG, "entering doWakeLock...");
        if(mWakeLock!=null){
            Log.d(TAG, "mWakeLock already initialized");
            if(mWakeLock.isHeld()) {
                Log.d(TAG, "mWakeLock release()...");
                mWakeLock.release(0);
            }
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Mqtt:WakelockTag");
        mWakeLock.acquire(SECONDS*1000); //timeout after x milliseconds
        Log.d(TAG, "mWakeLock acquired for "+SECONDS+" seconds");
    }
}
