package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class PhoneInfoActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info);
        addUUID();
        addEmail();
        addServer();
        addStartTime();
        addStopTime();
    }

    public void addUUID() {
        // Now read the desired content to a textview.
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String savedUUID = prefs.getString("app_uuid", " ");
        TextView loading_UUID = (TextView)findViewById(R.id.deviceid);
        loading_UUID.setText(savedUUID);
    }

    public void addEmail() {
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String email = prefs.getString("User_email", " ");
        TextView loading_email = (TextView)findViewById(R.id.usermail);
        loading_email.setText(email);
    }
    public void addServer() {
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String server = prefs.getString("Server_URL", " ");
        TextView loading_Server = (TextView)findViewById(R.id.server);
        loading_Server.setText(server);
    }
    public void addStartTime() {
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        Integer hoursStart = prefs.getInt("startHour", 00);
        Integer minutesStart = prefs.getInt("startMinute", 00);
        StringBuilder sb = new StringBuilder();
        sb.append(hoursStart).append(":");
        if (minutesStart<10){
            sb.append("0");
            sb.append(minutesStart);
        }
        else{sb.append(minutesStart);}
        String startTime = sb.toString();
        TextView loading_StartTime = (TextView)findViewById(R.id.startTime);
        loading_StartTime.setText(startTime);
    }
    public void addStopTime() {
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        Integer hoursStop = prefs.getInt("stopHour", 00);
        Integer minutesStop = prefs.getInt("stopMinute", 00);
        StringBuilder sb = new StringBuilder();
        sb.append(hoursStop).append(":");
        if (minutesStop<10){
            sb.append("0");
            sb.append(minutesStop);
        }
        else{sb.append(minutesStop);}
        String stopTime = sb.toString();
        TextView loading_StopTime = (TextView)findViewById(R.id.stopTime);
        loading_StopTime.setText(stopTime);
    }

}
