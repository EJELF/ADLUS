package com.android.edgarsjanovskis.adlus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service {
    public LocationService() {




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return Service. START_NOT_STICKY ; }



    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText (this , "Service is created!" ,Toast.LENGTH_LONG ).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
