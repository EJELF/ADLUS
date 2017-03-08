package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally {

                    Intent intent = new Intent(SplashScreen.this,Main2Activity.class);
                    // Šim jānovērš vairāku launcher ikonu veidošanu
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        //  Auto-generated method stub
        super.onPause();
        finish();
    }
}
