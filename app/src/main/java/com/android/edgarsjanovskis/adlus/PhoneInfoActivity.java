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
        // Now read the desired content to a textview.
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String email = prefs.getString("User_email", " ");

        TextView loading_email = (TextView)findViewById(R.id.usermail);
        loading_email.setText(email);
    }
    public void addServer() {
        // Now read the desired content to a textview.
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String server = prefs.getString("Server_URL", " ");

        TextView loading_Server = (TextView)findViewById(R.id.server);
        loading_Server.setText(server);
    }
    public void addStartTime() {
        // Now read the desired content to a textview.
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String startTime = prefs.getString("App_Start_Time", " ");

        TextView loading_Server = (TextView)findViewById(R.id.startTime);
        loading_Server.setText(startTime);
    }
    public void addStopTime() {
        // Now read the desired content to a textview.
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String stopTime = prefs.getString("App_Stop_Time", " ");

        TextView loading_Server = (TextView)findViewById(R.id.stopTime);
        loading_Server.setText(stopTime);
    }

}
