package com.android.edgarsjanovskis.adlus.model;

import com.google.android.gms.maps.model.LatLng;

public class MyGeofences {
    private LatLng mPosition;
    private Integer mGeofenceId;
    private String mTitle;
    private String mSnippet;

    public MyGeofences(int id, double lat, double lng) {
        mGeofenceId = id;
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
    }

    public MyGeofences(Integer id) {
        mGeofenceId = id;
        mPosition = null;
        mTitle = null;
        mSnippet = null;
    }

    public MyGeofences(){}

    public MyGeofences(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    public void setmGeofenceId(Integer mGeofenceId) {
        this.mGeofenceId = mGeofenceId;
    }

    public Integer getmGeofenceId() {
        return mGeofenceId;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() { return mTitle; }

    public String getSnippet() {
        return mSnippet;
    }

    /**
     * Set the title of the marker
     * @param title string to be set as title
     */

    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the description of the marker
     * @param snippet string to be set as snippet
     */

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }
}


