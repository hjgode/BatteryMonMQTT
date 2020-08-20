package com.sample.batterymonmqtt;

import android.content.Context;
import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class MqttPublisherHiveMQ {
    Context _context;
    final String TAG="MqttPublisherHiveMQ";

    MqttPublisherHiveMQ(Context context){
        _context=context;
    }

    public void doPublish(){
        Log.d(TAG, "doPublish()...");
        MySharedPreferences mySharedPreferences=new MySharedPreferences(_context);

        Log.d(TAG, "doPublish()...build client...");
        Mqtt3AsyncClient client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost(mySharedPreferences.getHost())
                .serverPort(mySharedPreferences.getPortInt())
                .buildAsync();
        Log.d(TAG, "doPublish()...client.connect()...");
        client.connect()
                .whenComplete((connAck, throwable) -> {
                    Log.d(TAG, "doPublish()...client,connect()...whenCompleted...");
                    if (throwable != null) {
                        // Handle connection failure
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...ERROR: "+throwable.getMessage());
                    } else {
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success");
                        // Setup subscribes or start publishing
                        String payloadS=MyJSON.getJSON(BatteryInfo.getBattInfo(_context));
                        byte[] payload=payloadS.getBytes();
                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...");
                        client.publishWith()
                                .topic("android/batteries/" + mySharedPreferences.getTopic())
                                .payload(payload)
                                .qos(MqttQos.AT_MOST_ONCE)
                                .retain(true)
                                .send()
                                .whenComplete((mqtt3Publish, throwable1) -> {
                                    if (throwable1 != null) {
                                        // Handle failure to publish
                                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...ERROR: "+throwable1.getMessage());
                                    } else {
                                        // Handle successful publish, e.g. logging or incrementing a metric
                                        Log.d(TAG, "doPublish()...client,connect()...whenCompleted...success...publish()...completed");            }
                                });
                    }
                });
        Log.d(TAG, "doPublish()...disconnect()...");
        client.disconnect()
                .whenComplete((aVoid, throwable2) -> {
                    if(throwable2!=null){
                        Log.d(TAG, "doPublish()...disconnect()...ERROR: "+throwable2.getMessage());
                    }else{
                        Log.d(TAG, "doPublish()...disconnect()...success");
                    }
                });
        Log.d(TAG, "doPublish()...END");
    }
}
