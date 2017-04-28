package com.android.edgarsjanovskis.adlus.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

class MyActivities {
    private String mPhoneId;
    private String mGeofence;
    private String mTransition;
    private String mActivityTimestamp;

    private boolean isPosted;
    public boolean isPosted() {
        return isPosted;
    }

    public void setPosted(boolean posted) {
        isPosted = posted;
    }


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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(new Date());
    }

}





