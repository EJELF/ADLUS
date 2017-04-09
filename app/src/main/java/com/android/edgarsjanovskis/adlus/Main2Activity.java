package com.android.edgarsjanovskis.adlus;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity {

    Context context;
    public static String uniqueID;
    public final String PREFS_NAME = "AdlusPrefsFile";
    final String PREF_VERSION_CODE_KEY = "version_code";
    public static final String LAST_DB_CHANGES = "LastChanges";
    public static final String LAST_UPDATE = "LastUpdate";
    public final String APP_UUID = "app_uuid";
    public final int DOESNT_EXIST = -1;
    SharedPreferences prefs;
    SharedPreferences sharedPref;

    TextView tvIsConnLabel;
    TextView tvLastChanges;
    TextView tvLastUpdate;
    RadioButton btnConnected;
    Color color = null;

    public static boolean isConnected = false;
/*
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        checkFirstRun();
        //checkConnection();

        // get reference to the views
        tvLastUpdate = (TextView)findViewById(R.id.tvLastUpdate);
        tvLastChanges = (TextView)findViewById(R.id.tvLastChanges);
        prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String lastchanges =  prefs.getString(LAST_DB_CHANGES, " ");
        tvLastChanges.setText(lastchanges);
        String lastupdate =  prefs.getString(LAST_UPDATE, " ");
        tvLastUpdate.setText(lastupdate);
        sharedPref = getSharedPreferences("MapPrefsFile", MODE_PRIVATE );


        ImageButton bClock = (ImageButton) findViewById(R.id.btnMfiles);
        bClock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri webpage = Uri.parse("https://www.mfiles.com");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                // Verify it resolves
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                // Start an activity if it's safe
                if (isIntentSafe) {
                    startActivity(webIntent);
                }
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        registerReceiver(NetworkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    protected void onStop(){
        super.onStop();
        unregisterReceiver(NetworkStatusReceiver);
    }

    ////////////////////
    private BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            isConnected = networkInfo !=null && networkInfo.isConnectedOrConnecting();

            tvIsConnLabel = (TextView) findViewById(R.id.tvIsConnLabel);
            btnConnected = (RadioButton) findViewById(R.id.isConnected);

            if(!isConnected){
                Toast noInternetToast = Toast.makeText(getApplicationContext(),R.string.no_internet, Toast.LENGTH_LONG);
                noInternetToast.show();
                btnConnected.setChecked(false);
                tvIsConnLabel.setText(R.string.no_internet);
            } else {
                btnConnected.setChecked(true);
                tvIsConnLabel.setText(R.string.isConnected);
            }
        }
    };

    /////////////////////
    public void checkFirstRun(){

        //Get current version code
        int currentVersionCode;

        try{
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e){
            //handle exception
            e.printStackTrace();
            return;
        }

        //Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        if (currentVersionCode == savedVersionCode){
            //This is just normal run
            return;
        } else if (savedVersionCode == DOESNT_EXIST){
            //this is a new install (of user cleared the shared prefs)
            Intent intent = new Intent(Main2Activity.this,WelcomeActivity.class);
            startActivity(intent);
            createUUID();

        } else if (currentVersionCode > savedVersionCode){
            // This is an upgrade. Pagaidām neizmantoju
            return;
        }
        // Update the shared prefs with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        prefs.edit().putString(APP_UUID, uniqueID).apply();
    }
    public void createUUID() {
        uniqueID = UUID.randomUUID().toString();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(APP_UUID, uniqueID).apply();

        Toast.makeText(getApplicationContext(), "UUID izveidots : " + uniqueID, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(NetworkStatusReceiver);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    public void butonShowMap_onClick(View view){
        Intent intent = new Intent(this, ShowMap.class);
        startActivity(intent);
    }

    public void buttonResponse_onClick (View view){
        Intent form = new Intent(this, GetMyProjects.class);
        startActivity(form);
    }

    public void buttonUUID_onClick (View view){
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        try{
            Map<String,?> keys = prefs.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                Log.d("map values",entry.getKey() + ": " +
                        entry.getValue());
            }

        }catch (Exception e){
            Log.e("Error: ", e.toString());
        }
        Intent intent = new Intent(this, PhoneInfoActivity.class);
        startActivity(intent);
    }

    public void buttonSettings_onClick (View view){
        Intent intent = new Intent(this, AppSettingsActivity.class);
        startActivity(intent);
    }

    public void buttonStartGeofencing_onClick (View view){
        // sāk GeofencingService pārbaudot vai nav sākta
        if(!isMyServiceRunning(GeofencingService.class)) {
            Intent intent = new Intent(this, GeofencingService.class);
            startService(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Service already running!", Toast.LENGTH_LONG).show();
        }
    }

    public void butonShowSQlite_onClick (View view){
        Intent intent = new Intent(this, GeofencingService.class);
        stopService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
