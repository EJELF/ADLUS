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

public class StartAlarmReceiver extends WakefulBroadcastReceiver {

    Calendar cal1;
    PendingIntent sender1;

    boolean isStarted;
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Log.println(Log.ERROR, TAG,"START_AlarmReceived");
       Intent geofencingServiceIntent = new Intent(context, GeofencingService.class);
            context.startService(geofencingServiceIntent);
            Log.i(TAG, "Service started from receiver!!!!");

        Intent intent1 = new Intent(context, Main2Activity.class);
        geofencingServiceIntent.putExtra("isStarted", isStarted = true);
        PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        wl.release();
    }

    @SuppressLint("NewApi")
    public void setStartAlarm(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        Integer hoursStart = prefs.getInt("startHour", 0);
        Integer minutesStart = prefs.getInt("startMinute", 0);

        cal1 = Calendar.getInstance();
        cal1.getTime();
        cal1.add(Calendar.DAY_OF_MONTH, 0);
        cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH), hoursStart-3, minutesStart);


        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //intents to call them by AlarmManeger
        Intent int1 = new Intent(context, StartAlarmReceiver.class);
        sender1 = PendingIntent.getBroadcast(context, 192837, int1, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), sender1);
    }


}
