package com.android.edgarsjanovskis.adlus;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static com.android.edgarsjanovskis.adlus.Constants.TAG;

public class StopAlarmReceiver extends WakefulBroadcastReceiver {
    private Calendar cal2;
    private PendingIntent sender2;

    private boolean isStarted;
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Log.println(Log.ERROR, TAG,"STOP_AlarmReceived");
        Intent geofencingServiceIntent = new Intent(context, GeofencingService.class);
        context.stopService(geofencingServiceIntent);
        Log.i(TAG, "Service stopped from receiver!!!!");

        Intent intent1 = new Intent(context, Main2Activity.class);
        geofencingServiceIntent.putExtra("isStarted", isStarted = false);
        PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        wl.release();
    }

    @SuppressLint("NewApi")
    public void setStopAlarm(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        Integer hoursStop = prefs.getInt("stopHour", 0);
        Integer minutesStop = prefs.getInt("stopMinute", 0);

        cal2 = Calendar.getInstance();
        cal2.getTime();
        cal2.add(Calendar.DAY_OF_MONTH, 0);
        cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), hoursStop-3, minutesStop);


        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //intents to call them by AlarmManeger
        Intent int2 = new Intent(context, StopAlarmReceiver.class);
        sender2 = PendingIntent.getBroadcast(context, 192837, int2, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), sender2);
    }

}
