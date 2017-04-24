package com.android.edgarsjanovskis.adlus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostVolleyIntentService extends IntentService {
    private static final String TAG = PostIntentService.class.getSimpleName();

    public PostVolleyIntentService() {
        super(TAG);
    }

    Context mContext;

    public String myurl = "";
    private Integer phoneId = 0;
    private SharedPreferences prefs;
    String mGeofence;
    String mTrigger;
    String json = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Post Volley service started");
        prefs = getApplicationContext().getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        phoneId = prefs.getInt("PhoneID", 0);
    }

    @Override
    protected void onHandleIntent(Intent postIntent) {
        // Instantiate the RequestQueue.
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
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
        //Try to use Volley
        final String URL = "http://" + myurl + "/api/Activities";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(1, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return json == null ? null : json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            json, "utf-8");
                    return null;
                }
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }
}