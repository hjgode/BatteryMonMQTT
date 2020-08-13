package com.sample.batterymonmqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Message;
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

    MqttMessage getMessage(String payload){
        MqttMessage message=new MqttMessage();
        byte[] payloadB=payload.getBytes();
        message.setQos(0); //0=do not wait for ACK, 1=repeat sending DUP messages until ACK once, 2=send and wait for ACK
        message.setRetained(true); //message will no be available at the broker all the time
        message.setPayload(payloadB);
        return message;
    }

    void doPublish(Context context, final BatteryInfo.BattInfo battInfo, final String myhost, final String port){
        Log.d(TAG, "doPublish()..., host="+myhost);
        final String sLevel=Integer.toString(battInfo.level);
        final String sCharging=(battInfo.charging?"charging":"discharging");

        String clientId = MqttClient.generateClientId();
        try {
//            MqttClient client=new MqttClient("tcp://"+myhost+":"+port, clientId);
            MqttAndroidClient client=new MqttAndroidClient(context, "tcp://"+myhost+":"+port, clientId);
            String devicemodel= Build.DEVICE + "-" + WifiReceiver.getMac(context);
            Log.d(TAG,"publish to android/batteries/"+devicemodel);
            MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setAutomaticReconnect(false);
            mqttConnectOptions.setCleanSession(true);
//            mqttConnectOptions.setServerURIs(new String[]{"tcp://"+myhost+":"+port});
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
//            client.connect(); //NO ASYNC calls
            IMqttToken token=client.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "mqtt connected to "+myhost);
                        MqttMessage message=getMessage(sLevel);

                    try {
                        client.publish("android/batteries/"+devicemodel+"/level", message);
                        message=getMessage(sCharging);
                        client.publish("android/batteries/"+devicemodel+"/status", message);
                        @SuppressLint({"NewApi", "LocalSuppress"}) String timestamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        message=getMessage(timestamp);

                        client.publish("android/batteries/"+devicemodel+"/datetime", message);
                        //do the JSON stuff
                        message=getMessage(MyJSON.getJSON(battInfo));
                        client.publish("android/batteries/"+devicemodel, message);

                        Log.d(TAG, "publish done...");
                        UpdateReceiver.sendMessage(context,timestamp);
                        UpdateReceiver.sendMessage(context, "publish done to tcp://"+myhost+":"+port);
                        UpdateReceiver.sendMessage(context, "android/batteries/"+devicemodel);
                        UpdateReceiver.sendMessage(context, battInfo.ToString());

                        client.unregisterResources();
                        client.close();
//                        client.disconnect();
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
                        e.printStackTrace();
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
