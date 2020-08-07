package com.sample.batterymonmqtt;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MQTTPublisher {
    String host="192.168.0.40"; //needs to be completed to tcp://...
    public MQTTPublisher(String Host){
        host=Host;
    }

    void doPublish(Context context, final BatteryInfo.BattInfo battInfo, final String myhost){
        Log.d(TAG, "doPublish()..., host="+myhost);
        final String sLevel=Integer.toString(battInfo.level);
        final String sCharging=(battInfo.charging?"charging":"discharging");

        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client=new MqttAndroidClient(context, "tcp://"+myhost, clientId);
        try {
            MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
            mqttConnectOptions.setConnectionTimeout(30);
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setAutomaticReconnect(false);
            mqttConnectOptions.setServerURIs(new String[]{"tcp://"+myhost});
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            IMqttToken token=client.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "mqtt connected");
                    MqttMessage message=new MqttMessage(sLevel.getBytes());
                    message.setQos(2);
                    message.setRetained(true);
                    try {
                        client.publish("android/batteries/"+android.os.Build.MODEL+"/level", message).setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d(TAG, "client.publish success");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d(TAG, "client.publish failed: "+ exception.getMessage());
                            }
                        });
                        message.setPayload(sCharging.getBytes());
                        client.publish("android/batteries/"+android.os.Build.MODEL+"/status", message).setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d(TAG, "client.publish success");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d(TAG, "client.publish failed: "+ exception.getMessage());
                            }
                        });
                        message.setPayload(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).getBytes());
                        client.publish("android/batteries/"+android.os.Build.MODEL+"/datetime", message).setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d(TAG, "client.publish success");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d(TAG, "client.publish failed: "+ exception.getMessage());
                            }
                        });

                        if(client!=null){
                            client.disconnect().setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    Log.d(TAG, "mqtt disconnected");
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Log.d(TAG, "mqtt disconnect failed");
                                }
                            });
                        }

                    } catch (MqttException e) {
                        Log.e(TAG, "doPublish: "+e.getMessage());

                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "mqtt connect failed: "+exception.getMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "doPublish: "+e.getMessage());
        }
    }

}
