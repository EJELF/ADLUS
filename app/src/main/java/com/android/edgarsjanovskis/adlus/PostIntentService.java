package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostIntentService extends IntentService {
    private static final String TAG = PostIntentService.class.getSimpleName();

    public PostIntentService() {super(TAG);}

    Context mContext;

    public String myurl = "";
    private Integer phoneId = 0;
    private SharedPreferences prefs;
    String mGeofence;
    String mTrigger;
    String json = null;
    InputStream inputStream;
    HttpURLConnection urlConnection;
    byte[] outputBytes;
    String ResponseData = "";
    int statusCode;


    @Override
    public void onCreate() {
        super.onCreate();
        // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        // JUST DO IN BACKGROUND
        //Toast.makeText(getApplicationContext(), "Post Service started!", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Post service started");
        prefs = getApplicationContext().getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        phoneId = prefs.getInt("PhoneID", 0);
    }


    @Override
    protected void onHandleIntent(Intent postIntent) {

        //if (intent != null) {
        // This describes what will happen when service is triggered
        System.out.println("POST INTENT RECEIVED");

        mGeofence = postIntent.getStringExtra("mGeofence");
        mTrigger = postIntent.getStringExtra("mTrigger");
        Log.i(TAG, "Extras received: " + mGeofence + " ; " + mTrigger);


        final String currentDateTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        currentDateTime = sdf.format(new Date());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("geofenceID", mGeofence);
            jsonObject.accumulate("phoneID", phoneId);
            jsonObject.accumulate("transitionStateID", mTrigger);
            jsonObject.accumulate("dateTime", currentDateTime);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Json created: " + json);

        if (isConnected() && checkNetworks()) {

            try {
                URL url = new URL("http://" + myurl + "/api/Activities");
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setReadTimeout(5500);
                //urlConnection.setConnectTimeout(5500);
                outputBytes = json.getBytes("UTF-8");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.addRequestProperty("Accept", "application/json");
                urlConnection.connect();
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputBytes);
                os.close();

                statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_CREATED) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    ResponseData = convertStreamToString(inputStream);
                } else {
                    ResponseData = null;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "Response: " + ResponseData);
            // }else {
            // Log.e(TAG, " NO INTENT RECEIVED");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean checkNetworks() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();

        Log.i(TAG, "Network count: " + networks.length);
        for (int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);

            Log.i(TAG, "Network " + i + ": " + networks[i].toString());
            Log.i(TAG, "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            Log.i(TAG, "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));
            if(caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                return true;
            }
        }
        return false;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
