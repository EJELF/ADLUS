package com.android.edgarsjanovskis.adlus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Edgars on 18.12.16.
 */
public class StoreLocation {
    public LatLng mLatLng;
    public String mId;
    StoreLocation(LatLng latlng, String id) {
        mLatLng = latlng;
        mId = id;
    }
}
