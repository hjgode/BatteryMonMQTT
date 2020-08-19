# Battery Monitor for MQTT
BatteryMonMQTT is a small tool that publishes battery data peridically to a MQTT broker, for example mosquitto.

![Main Screen](https://github.com/hjgode/BatteryMonMQTT/raw/master/doc/main_screen.png)

The periodic action is implemented as a JobService which is scheduled by a JobScheduler. The JobService will not be started, if the device is in Doze mode, but then runs later on inside the Maintenance window of the device. So the app does not cosume much battery.

The JobService is also registered after booting the device. If the app is setup once, you do not need to care about the function.

The only settings you need to do are the topic and the server name or ip.

![Settings Screen](https://github.com/hjgode/BatteryMonMQTT/raw/master/doc/settings_screen.png)

The tool does not provide secured MQTT although the heavy Paho MQTT library supports that. Feel free to clone the repo and implement user/password.

I started the app thinking it can not be that hard to create a simple periodic update to MQTT. But the devil lives in the details. Paho MQTT is very heavy and needs many additional permissions added to the app. It leaks a service connection after disconnect.

The other hard coding is due to Google's paranoia and the restrictions you will face writing a periodic background service. Especially starting with Android 8.

You may download the 1.0 release with this QR code:
![SDownload BatteryMonMQTT v1.0](https://github.com/hjgode/BatteryMonMQTT/blob/master/app/release/BatteryMonMqtt_10.apk?raw=true)

Have fun