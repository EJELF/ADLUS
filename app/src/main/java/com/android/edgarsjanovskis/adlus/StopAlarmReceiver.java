package com.android.edgarsjanovskis.adlus;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static com.android.edgarsjanovskis.adlus.Constants.TAG;

public class StopAlarmReceiver extends WakefulBroadcastReceiver {
    boolean isStarted;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.println(Log.ERROR, TAG,"STOP_AlarmReceived");
        Intent geofencingServiceIntent = new Intent(context, GeofencingService.class);
        context.stopService(geofencingServiceIntent);
        Log.i(TAG, "Service stopped from receiver!!!!");

        Intent intent1 = new Intent(context, Main2Activity.class);
        geofencingServiceIntent.putExtra("isStarted", isStarted = false);
        PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
