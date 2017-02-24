package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import static com.android.edgarsjanovskis.adlus.MainActivity.uniqueID;

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
        loading_UUID.setText(uniqueID);
    }



}
