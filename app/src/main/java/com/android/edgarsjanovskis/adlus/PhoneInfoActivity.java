package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import static com.android.edgarsjanovskis.adlus.MainActivity.getUniqueID;

/**
 * Created by Edgars on 13.02.17.
 */

public class PhoneInfoActivity extends Activity {


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info);
        showUUID();
    }

    public void showUUID() {
        // Now read the desired content to a textview.

        TextView loading_UUID = (TextView)findViewById(R.id.deviceid);
        if (getUniqueID() == null){
            loading_UUID.setText(getUniqueID());
        }
        else loading_UUID.setText(" Nav izveidots UUID");
        //SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //String savedVersionCode = prefs.getString(APP_UUID, " ");

    }



}
