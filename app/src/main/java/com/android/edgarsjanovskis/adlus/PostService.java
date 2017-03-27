package com.android.edgarsjanovskis.adlus;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Edgars on 25.03.17.
 */

public class PostService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PostService(String name) {
        super("post-service");
    }

    @Override
    public void onCreate(){
        super.onCreate();// if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // This describes what will happen when service is triggered


    }
}
