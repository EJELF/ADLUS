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

}
