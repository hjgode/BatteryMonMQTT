package com.sample.batterymonmqtt;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.sample.batterymonmqtt.MainActivity.TAG;

public class MyJSON {
    String level="";
    String status="";
    String datetime="";
    public MyJSON(){

    }
    public static String getJSON(BatteryInfo.BattInfo battInfo){
        Log.d(TAG, "getJSON for  "+ battInfo.toString());
        String jsonString="";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        //String timestamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("level", battInfo.level);
            jsonObject.put("status", (battInfo.charging?"charging":"discharging"));
            jsonObject.put("datetime", timestamp);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        jsonString=jsonObject.toString();
        Log.d(TAG, "JSON is "+jsonString);
        return jsonString;
    }
}
