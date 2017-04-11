package com.android.edgarsjanovskis.adlus.model;
/*
public class MyGeofences{
        private Integer mGeofenceId;
        private Double mLattitude;
        private Double mLongitude;
        private String mTitle;
        private String mSnippet;

    public MyGeofences(Double lat, Double lng, Integer geofenceId, String lr) {
    }
    public MyGeofences(){}

    public Integer getmId(){return mGeofenceId;}
    public void setmId(Integer id){ this.mGeofenceId =id ;}
    public Double getmLattitude(){return mLattitude;}
    public void setmLattitude(Double lat){this.mLattitude = lat;}
    public Double getmLongitude(){return mLongitude;}
    public void setmLongitude(Double lng){this.mLongitude = lng;}
    public String getmTitle(){return mTitle;}
    public void setmTitle(String title){this.mTitle = title;}
    public String getmSnippet(){return mSnippet;}
    public void setmSnippet(String snippet){this.mSnippet = snippet;}
}
*/

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


