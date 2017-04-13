package com.android.edgarsjanovskis.adlus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {
    StartAlarmReceiver startAlarmReceiver = new StartAlarmReceiver();
    StopAlarmReceiver stopAlarmReceiver = new StopAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            startAlarmReceiver.setStartAlarm(context);
            stopAlarmReceiver.setStopAlarm(context);
        }
    }
}
