package com.android.edgarsjanovskis.adlus;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.edgarsjanovskis.adlus.model.MyGeofences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.android.edgarsjanovskis.adlus.Constants.GEOFENCE_EXPIRATION_TIME;
import static com.android.edgarsjanovskis.adlus.Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.CUSTODIAN_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.CUSTODIAN_PHONE_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.EMPLOYEE_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.GEOFENCE_ID_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.IMEI_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.LATITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.LONGITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.PHONE_ID_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.PROJECT_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.PROJECT_LR_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.RADIUS_COLUMN;
import static com.android.edgarsjanovskis.adlus.ProjectsHelper.TS_COLUMN;
import static com.android.edgarsjanovskis.adlus.R.id.list;

//import static com.android.edgarsjanovskis.adlus.ProjectsHelper.KEY_ID;


public class ShowMap extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        //GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {

    ////
    private static final String TAG = com.android.edgarsjanovskis.adlus.GeofencingActivity.class.getSimpleName();
    private static final boolean DEVELOPER_MODE = true;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private TextView textLat, textLong;

    private MapFragment mapFragment;
    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    List<Geofence> mGeofenceList;
    List<LatLng> mLatLngList;
    MyGeofences geofence;

    private PendingIntent mGeofencePendingIntent;  // nezinu vai vajadzēs
    private GeofencingRequest mGeofenceRequest;

    public ProjectsHelper mDbHelper;
    public SQLiteDatabase db;

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, com.android.edgarsjanovskis.adlus.GeofencingActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }

    // tomēr nenovērsa Unable to get provider com.google.firebase.provider.FirebaseInitProvider kļūdu
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);
        //Realm.init(this); //initialize other plugins
        setContentView(R.layout.activity_map);

        // !!!!! šie rādīs patreizējo atrašanos
        textLat = (TextView) findViewById(R.id.lat);
        textLong = (TextView) findViewById(R.id.lon);

        //start new instance of ProjectsHelper and reader
        //databaseHelper = new ProjectsHelper(this);

        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<>();
        mLatLngList = new ArrayList<>();

        // initialize GoogleMaps
        initGMaps();

        // create GoogleApiClient
        createGoogleApi();

        //šis bija menu sastāvā lai uzsāktu izsekošanu
        startGeofences();

    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity // if pieliku es
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate( R.menu.main_menu, menu );
            return true;
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch ( item.getItemId() ) {
            case R.id.geofence: {
                startGeofence();
                return true;
            }
            case R.id.clear: {
                clearGeofence();
                return true;
            }*/
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final int REQ_PERMISSION = 999;

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getLastKnownLocation();

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
        Toast toast = Toast.makeText(getApplicationContext(), "ADLUS ir nepieciešama Jūsu atļauja piekļūt lokācijai", Toast.LENGTH_LONG);
        toast.show();
        setResult(0);
        finish();
    }

    // Initialize GoogleMaps
    private void initGMaps() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        // map.setOnMapClickListener(this); We dont use clicks on map
        //map.setOnMarkerClickListener(this);
    }

    /*
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "onMapClick(" + latLng + ")");
            markerForGeofence(latLng);
        }
    */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    private final int UPDATE_INTERVAL =  10000;  //3 min
    private final int FASTEST_INTERVAL = 1000;   //30 sek

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // GoogleApiClient.ConnectionCallbacks connected
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, mGeofenceRequest, mGeofencePendingIntent);
        Toast.makeText(this, getString(R.string.start_geofence_service), Toast.LENGTH_SHORT).show();
        //finish();
        //startLocationUpdates();
        //startGeofences();
        getLastKnownLocation();
        recoverGeofenceMarker();
    }


    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
        googleApiClient.connect(); // ieliku es
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }

    private void writeActualLocation(Location location) {
        textLat.setText("Lat: " + location.getLatitude());
        textLong.setText("Long: " + location.getLongitude());

        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private Marker locationMarker;

    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");

        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if (map != null) {
            if (locationMarker != null)
                locationMarker.remove();

            locationMarker = map.addMarker(markerOptions);
            float zoom = 9f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private Marker geoFenceMarker;

    private void markerForGeofence(List<LatLng> mLatLngList) {


        for (LatLng position : mLatLngList) {

            LatLng latLng = new LatLng(position.latitude, position.longitude);
            Log.i(TAG, "markerForGeofence(" + latLng + ")");
            String title = position.latitude + "," + position.longitude;
            // Define marker options

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(title);

            if (map != null) {
                // Remove last geoFenceMarker
                //if (geoFenceMarker != null)
                //geoFenceMarker.remove();
                geoFenceMarker = map.addMarker(markerOptions);

            }
        }
    }


    // Start Geofence creation process //Pārsaucu uz StartGeofences , jo ir saraksts
    private void startGeofences() {

        Log.i(TAG, "startGeofences()");
        if (geoFenceMarker != null) {

            GeofencingRequest geofenceRequest = createGeofencesRequest();
            addGeofences(geofenceRequest);
            Log.e(TAG, "Geofence marker is NOT null");

            //} else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofencesRequest() {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(mGeofenceList)   // List
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencesPendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofences(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    mGeofenceRequest,
                    createGeofencesPendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);

        if (status.isSuccess()) {
            markerForGeofence(mLatLngList); //????????
            //saveGeofence();
            //drawGeofence(mLatLngList);
        } else {
            Log.i(TAG, "nav ko uzzīmēt!!!" + status);
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;

    private void drawGeofence(List<LatLng> mLatLngList) {
        for (LatLng latLng : mLatLngList) {
            Log.d(TAG, "drawGeofence()");

            if (geoFenceLimits != null)
                geoFenceLimits.remove();

            CircleOptions circleOptions = new CircleOptions()
                    .center(geoFenceMarker.getPosition())
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150))
                    .radius(100);
            geoFenceLimits = map.addCircle(circleOptions);
        }
    }

    // Recovering last Geofence marker
    private void recoverGeofenceMarker() {
        Log.d(TAG, "recoverGeofenceMarker");
        markerForGeofence(mLatLngList);
        //drawGeofence(mLatLngList);
    }

    // Clear Geofence
    private void clearGeofences() {
        Log.d(TAG, "clearGeofences()");
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                geoFencePendingIntent
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    // remove drawing
                    removeGeofenceDraw();
                }
            }
        });
    }

    private void removeGeofenceDraw() {
        Log.d(TAG, "removeGeofenceDraw()");
        if (geoFenceMarker != null)
            geoFenceMarker.remove();
        if (geoFenceLimits != null)
            geoFenceLimits.remove();
    }

    @Override
    public void onResume() {
        super.onResume();

        int id, phoneId;
        int geofenceId;
        float radius;
        double lat;
        double lon;
        Timestamp lastUpdate;
        String projectLr, timestamp, employee, customer, project, imei, custodian, custodianPhone;
        ListView lv = (ListView) findViewById(list);
        //start new instance of ProjectsHelper and reader

        mDbHelper = new ProjectsHelper(this);
        Cursor reader = mDbHelper.getAllRecordList();

        // ar if novērš kļūdu, kad android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        //if (reader != null)
        for (reader.moveToFirst(); !reader.isAfterLast(); reader.moveToNext()) {
            //id = reader.getInt(reader.getColumnIndex(KEY_ID));
            geofenceId = reader.getInt(reader.getColumnIndex(GEOFENCE_ID_COLUMN));
            projectLr = reader.getString(reader.getColumnIndex(PROJECT_LR_COLUMN));
            lat = reader.getDouble(reader.getColumnIndex(LATITUDE_COLUMN));
            lon = reader.getDouble(reader.getColumnIndex(LONGITUDE_COLUMN));
            radius = reader.getFloat(reader.getColumnIndex(RADIUS_COLUMN));
            phoneId = reader.getInt(reader.getColumnIndex(PHONE_ID_COLUMN));
            imei = reader.getString(reader.getColumnIndex(IMEI_COLUMN));
            employee = reader.getString(reader.getColumnIndex(EMPLOYEE_COLUMN));
            customer = reader.getString(reader.getColumnIndex(PROJECT_COLUMN));
            timestamp = reader.getString(reader.getColumnIndex(TS_COLUMN));
            custodian = reader.getString(reader.getColumnIndex(CUSTODIAN_COLUMN));
            custodianPhone = reader.getString(reader.getColumnIndex(CUSTODIAN_PHONE_COLUMN));
            //lastUpdate = Timestamp.valueOf(reader.getString(reader.getColumnIndex(LAST_DB_UPDATE)));

            ////////////////////////////////////////////////////////////// pamēģināšu šeit
            LatLng latLng = new LatLng(lat, lon);
            Geofence fence = new Geofence.Builder()
                    .setRequestId(String.valueOf(geofenceId))
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setExpirationDuration(GEOFENCE_EXPIRATION_TIME)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(fence);
            mLatLngList.add(latLng);
            Log.e("izveidots ", fence.toString());

        }
        if (reader.isAfterLast())
            reader.close();
        //////////////////////////////////////////////////////////////
    }

    @Override
    public void onPause(){
        super.onPause();
        if(db != null){
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        if(db != null) {
            db.close();
        }
        super.onDestroy();
    }

}
