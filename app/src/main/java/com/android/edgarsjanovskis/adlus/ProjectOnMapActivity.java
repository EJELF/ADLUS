package com.android.edgarsjanovskis.adlus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProjectOnMapActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    private static final String TAG = ProjectOnMapActivity.class.getSimpleName();

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private TextView textLat, textLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        textLat = (TextView) findViewById(R.id.lat);
        textLong = (TextView) findViewById(R.id.lon);
        this.setTitle("your_format_string, your_personal_text_to_display");
        // initialize GoogleMaps
        initGMaps();
        // create GoogleApiClient
        createGoogleApi();
    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

        private final int REQ_PERMISSION = 9;
    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                //noinspection StatementWithEmptyBody
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // close app and warn user was in to-do list
        Toast toast = Toast.makeText(getApplicationContext(),"ADLUS ir nepieciešama Jūsu atļauja piekļūt lokācijai", Toast.LENGTH_LONG);
        toast.show();
        setResult(0);
        finish();
    }

    // Initialize GoogleMaps
    private void initGMaps(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        // map.setOnMapClickListener(this); We dont use clicks on map
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }
    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        markerLocationFromIntent();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    private Marker locationMarker;
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
        //paša veidots
    @SuppressLint("SetTextI18n")
    private void markerLocationFromIntent() {

        Intent iin = getIntent();
        Bundle b= iin.getExtras();
        if(b !=null){
            String latit = (String) b.get("lat");
            String longit = (String) b.get("lng");
            String lr = (String) b.get("lr");
            String radius = (String) b.get("radius");
            textLat.setText("Lat: " +latit);
            textLong.setText("Long: " + longit);
            this.setTitle(lr + " atrašanās vieta kartē");

            MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latit), Double.parseDouble(longit)))
                .title(lr);

        if ( map!=null ) {
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);

            CircleOptions circleOptions = new CircleOptions()
                    .center(locationMarker.getPosition())
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150))
                    .radius(Integer.parseInt(radius));
            geoFenceLimits = map.addCircle(circleOptions);

            float zoom = 16f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((new LatLng(Double.parseDouble(latit), Double.parseDouble(longit))), zoom);
            map.animateCamera(cameraUpdate);
            }
        }
    }
}
