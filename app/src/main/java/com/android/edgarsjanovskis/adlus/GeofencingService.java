package com.android.edgarsjanovskis.adlus;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.android.edgarsjanovskis.adlus.Constants.GEOFENCE_EXPIRATION_TIME;
import static com.android.edgarsjanovskis.adlus.Constants.TAG;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.GEOFENCE_ID_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.LATITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.LONGITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.PROJECT_LR_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.RADIUS_COLUMN;

public class GeofencingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GeofencingService() {}

    boolean isRunning = true;
    //MediaPlayer player;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;

    List<Geofence> mGeofenceList;
    List<LatLng> mLatLngList;

    private PendingIntent mGeofencePendingIntent;
    private GeofencingRequest mGeofenceRequest;

    public DatabaseHelper mDbHelper;
    public SQLiteDatabase db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service is started!", Toast.LENGTH_LONG).show();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (isRunning) {
                                startLocationUpdates();
                                startGeofences();
                                Thread.sleep(5000);
                            }
                        } catch (Exception e) {
                            Log.e("Exception ", e.toString());
                        }
                    }
                }
        ).start();
        return  Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service is created!", Toast.LENGTH_LONG).show();
        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<>();
        mLatLngList = new ArrayList<>();

        int geofenceId;
        float radius;
        double lat;
        double lon;
        String lr;

        mDbHelper = new DatabaseHelper(this);
        Cursor reader = mDbHelper.getAllRecordList();
////////////////////////////////////
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        if (reader != null)
            for (reader.moveToFirst(); !reader.isAfterLast(); reader.moveToNext()) {
                geofenceId = reader.getInt(reader.getColumnIndex(GEOFENCE_ID_COLUMN));
                lr = reader.getString(reader.getColumnIndex(PROJECT_LR_COLUMN));
                lat = reader.getDouble(reader.getColumnIndex(LATITUDE_COLUMN));
                lon = reader.getDouble(reader.getColumnIndex(LONGITUDE_COLUMN));
                radius = reader.getFloat(reader.getColumnIndex(RADIUS_COLUMN));

                LatLng latLng = new LatLng(lat, lon);
                Geofence fence = new Geofence.Builder()
                        .setRequestId(String.valueOf(geofenceId) + " (" + lr +")")
                        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                        .setExpirationDuration(GEOFENCE_EXPIRATION_TIME)
                        .setLoiteringDelay(1000 * 60) // for DWELL
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();
                mGeofenceList.add(fence);
                mLatLngList.add(latLng);
                Log.e("izveidots ", fence.toString());
                if (reader.isAfterLast())
                    reader.close();
            }
    }

    @Override
    public void onDestroy() {
        //closing db
        if (db != null) {
            db.close();
        }
        //player.stop();
        stopLocationUpdates();
        stopGeofences();
        stopService();
        Toast.makeText(this, "Service is destroyed!", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    private void stopService(){
        isRunning = false;
        stopSelf();
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    private final int UPDATE_INTERVAL = 3*60*100;  //3 min 3*60*100
    private final int FASTEST_INTERVAL = 30*1000;   //30 sek 30*1000

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates(){
        Log.i(TAG, "stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            Log.i(TAG, "LastKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            //writeLastLocation();
            startLocationUpdates();
        } else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();
        }
    }

    // Start Geofence creation process //Pārsaucu uz StartGeofences , jo ir saraksts
    private void startGeofences() {
        Log.i(TAG, "startGeofences()");
        GeofencingRequest geofenceRequest = createGeofencesRequest();
        addGeofences(geofenceRequest);
        Log.e(TAG, "Geofence marker is NOT null");
    }

    private void stopGeofences() {
        Log.i(TAG, "stopGeofences()");
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, mGeofencePendingIntent);
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofencesRequest() {
        Log.d(TAG, "createGeofenceRequest");
            return new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) // Dwell to start cheching even if in geofence
                    .addGeofences(mGeofenceList)   // List
                    .build();
    }

    // private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencesPendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofences(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                mGeofenceRequest,
                createGeofencesPendingIntent());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");

        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        mGeofencePendingIntent = createGeofencesPendingIntent();
        mGeofenceRequest = createGeofencesRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, mGeofenceRequest, mGeofencePendingIntent);
        Toast.makeText(this, getString(R.string.start_geofence_service), Toast.LENGTH_SHORT).show();
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
        //googleApiClient.connect(); // ieliku es
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
    }


}
