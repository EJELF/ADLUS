package com.android.edgarsjanovskis.adlus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.edgarsjanovskis.adlus.Constants.TAG;


public class AlarmReceiver extends BroadcastReceiver {

    private static BroadcastReceiver tickReceiver;

    DateFormat formatter = new SimpleDateFormat("HH:mm");

    Date startT;
    Date stopT;
    Date now;

    @Override
    public void onReceive (Context context, Intent intent) {

        tickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0){
                    now = new Date();
                }
            }
        };

        Date time = new Date();
        String fnow = formatter.format(time);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            int startHours = extras.getInt("hoursStart");
            int startMinutes = extras.getInt("minutesStart");
            int stopHours = extras.getInt("hoursStop");
            int stopMinutes = extras.getInt("minutesStop");

            StringBuilder sb1 = new StringBuilder();
            sb1.append(startHours).append(":").append(startMinutes);
            String startTime = sb1.toString();
            Log.e("StartTime fromIntent: ", startTime);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(stopHours).append(":").append(stopMinutes);
            String stopTime = sb2.toString();
            Log.e("StopTime fromIntent: ", stopTime);
            try {
                startT = formatter.parse(startTime);
                stopT = formatter.parse(stopTime);
                now = formatter.parse(fnow);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("StartTime parsed: ", String.valueOf(startT));
            Log.e("StopTime parsed: ", String.valueOf(stopT));
            Log.e("CurrentTime: ", String.valueOf(now));
            int compareStart = now.compareTo(startT);
            int compareStop = now.compareTo(stopT);
            Log.e("CompareStart: ", String.valueOf(compareStart));
            Log.e("CompareStop: ", String.valueOf(compareStop));
        }

        Intent i = new Intent(context, GeofencingService.class);
        i.setAction("com.android.edgarsjanovskis.adlus.TIME_BROADCAST");

        if (now.compareTo(startT) >= 0 && now.compareTo(stopT) <= 0) {

            context.startService(i);
            Log.i(TAG, "Service started?????");
        } else {
            context.stopService(i);
        }

    }

}
