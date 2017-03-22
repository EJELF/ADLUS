package com.android.edgarsjanovskis.adlus.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Edgars on 15.03.17.
 */

public class MyActivities {
    private String mPhoneId;
    private String mGeofence;
    private String mTransition;
    private String mActivityTimestamp;

    public MyActivities(){}

    public String getmPhoneId() {
        return mPhoneId;
    }

    public void setmPhoneId(String mPhoneId) {
        this.mPhoneId = mPhoneId;
    }

    public String getmGeofence() {
        return mGeofence;
    }

    public void setmGeofence(String mGeofence) {
        this.mGeofence = mGeofence;
    }

    public String getmTransition() {
        return mTransition;
    }

    public void setmTransition(String mTransition) {
        this.mTransition = mTransition;
    }

    public String getmActivityTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        return currentDateTime;
    }

    public void setmActivityTimestamp(String mActivityTimestamp) {

    }
}





