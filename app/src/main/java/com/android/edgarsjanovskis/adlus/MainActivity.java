package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseControler dbControler;
    public static String uniqueID;
    public final String PREFS_NAME = "AdlusPrefsFile";
    final String PREF_VERSION_CODE_KEY = "version_code";
    public final String APP_UUID = "app_uuid";
    public final int DOESNT_EXIST = -1;

    EditText etResponse;
    TextView tvIsConnected;

    //statisko metodi izmanto lai citā aktivitātē iegūtu vērtību
    public static String getUniqueID() {
        return uniqueID;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();

        dbControler = new DatabaseControler(this);
        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText(R.string.connected);
        }
        else{
            tvIsConnected.setText(R.string.not_connected);
        }
    }

    public void checkFirstRun(){

        //Get current version code
        int currentVersionCode = 0;

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
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(APP_UUID, uniqueID).apply();

        Toast.makeText(getApplicationContext(), "UUID izveidots : " + uniqueID, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    public void butonShowMap_onClick(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void buttonResponse_onClick (View view){
        Intent form = new Intent(this, MyProjects.class);
        startActivity(form);
    }

    public void buttonUUID_onClick (View view){
        Intent intent = new Intent(this, PhoneInfoActivity.class);
        startActivity(intent);
    }

    public void buttonSettings_onClick (View view){
        Intent intent = new Intent(this, AppSettingsActivity.class);
        startActivity(intent);
    }

    public void buttonVersion_onClick (View view){
        try{
         int  currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            Toast.makeText(this, "Version code: " + currentVersionCode, Toast.LENGTH_LONG).show();
        } catch (android.content.pm.PackageManager.NameNotFoundException e){
            //handle exception
            e.printStackTrace();
        }
    }


}
