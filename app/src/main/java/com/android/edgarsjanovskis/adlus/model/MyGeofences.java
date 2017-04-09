package com.android.edgarsjanovskis.adlus.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyGeofences implements ClusterItem {
    private final LatLng mPosition;
    private Integer mGeofenceId;
    private String mTitle;
    private String mSnippet;

    public MyGeofences(int id, double lat, double lng) {
        mGeofenceId = id;
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
    }

    public MyGeofences( double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
    }

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

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() { return mTitle; }

    @Override
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
