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

    public MQTTPublisher(){
    }

    void doPublish(Context context, final BatteryInfo.BattInfo battInfo, final String myhost){
        Log.d(TAG, "doPublish()..., host="+myhost);
        final String sLevel=Integer.toString(battInfo.level);
        final String sCharging=(battInfo.charging?"charging":"discharging");

        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client=new MqttAndroidClient(context, "tcp://"+myhost, clientId);
        String devicemodel= Build.DEVICE + Build.ID;
        Log.d(TAG,"publish to android/batteries/"+devicemodel);
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
                    Log.d(TAG, "mqtt connected to "+myhost);
                    try {
                        MqttMessage message=new MqttMessage();
                        message.setQos(0); //0=do not wait for ACK, 1=repeat sending DUP messages until ACK once, 2=send and wait for ACK
                        message.setRetained(true); //message will no be available at the broker all the time
                        message.setPayload(sLevel.getBytes());
                        Log.d(TAG, "###publish level="+sLevel);
                        client.publish("android/batteries/"+devicemodel+"/level", message);
//                        client.publish("android/batteries/"+devicemodel+"/level", message).setActionCallback(new IMqttActionListener() {
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//                                Log.d(TAG, "client.publish success: "+asyncActionToken.toString());
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                Log.d(TAG, "client.publish failed:  "+asyncActionToken.toString() +", "+ exception.getMessage());
//                            }
//                        });
                        MqttMessage messageStatus=new MqttMessage();
                        messageStatus.setQos(0); //0=do not wait for ACK, 1=repeat sending DUP messages until ACK once, 2=send and wait for ACK
                        messageStatus.setRetained(true); //message will no be available at the broker all the time
                        messageStatus.setPayload(sCharging.getBytes());
                        client.publish("android/batteries/"+devicemodel+"/status", messageStatus);
//                        client.publish("android/batteries/"+devicemodel+"/status", message).setActionCallback(new IMqttActionListener() {
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//                                Log.d(TAG, "client.publish success "+asyncActionToken.toString());
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                Log.d(TAG, "client.publish failed:  "+asyncActionToken.toString()+", "+ exception.getMessage());
//                            }
//                        });
                        MqttMessage messageTime=new MqttMessage();
                        messageTime.setQos(0); //0=do not wait for ACK, 1=repeat sending DUP messages until ACK once, 2=send and wait for ACK
                        messageTime.setRetained(true); //message will no be available at the broker all the time
                        messageTime.setPayload(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).getBytes());
                        client.publish("android/batteries/"+devicemodel+"/datetime", messageTime);
//                        client.publish("android/batteries/"+devicemodel+"/datetime", message).setActionCallback(new IMqttActionListener() {
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//                                Log.d(TAG, "client.publish success "+asyncActionToken.toString());
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                Log.d(TAG, "client.publish failed:  "+asyncActionToken.toString()+", "+ exception.getMessage());
//                            }
//                        });

                        Log.d(TAG, "publish done...");
                        if(client!=null){
                            client.disconnect().setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    Log.d(TAG, "mqtt disconnected from "+myhost);
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Log.d(TAG, "mqtt disconnect failed "+myhost);
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
