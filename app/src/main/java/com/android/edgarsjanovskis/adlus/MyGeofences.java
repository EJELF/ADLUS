package com.android.edgarsjanovskis.adlus;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyGeofences extends AppCompatActivity {

    private String TAG = MyGeofences.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    //private static int phoneID = 5;

    // URL to get contacts JSON
    private static String url = "http://192.168.10.86:5111/api/AndroidDbUpdates";

    ArrayList<HashMap<String, String>> geofencesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofencesList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        new GetGeofences().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetGeofences extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyGeofences.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray geofences = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < geofences.length(); i++) {
                        JSONObject c = geofences.getJSONObject(i);

                        String id = c.getString("$id");
                        String geofenceId = c.getString("GeofenceID");
                        String lr = c.getString("LR");
                        String lat = c.getString("Latitude");
                        String lng = c.getString("Longitude");
                        String radius = c.getString("Radius");
                        String imei = c.getString("IMEI");
                        String employee = c.getString("EmployeeName");
                        String customer = c.getString("CustomerName");
                        String projectName = c.getString("ProjectName");
                        String ts = c.getString("ts");

/*
                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");
*/
                        // tmp hash map for single contact
                        HashMap<String, String> geofence = new HashMap<>();
                        // adding each child node to HashMap key => value
                        geofence.put("$id", id);
                        geofence.put("GeofenceID", geofenceId);
                        geofence.put("LR", lr);
                        geofence.put("Latitude", lat);
                        geofence.put("Longitude", lng);
                        geofence.put("Radius", radius);
                        geofence.put("IMEI", imei);
                        geofence.put("EmployeeName", employee);
                        geofence.put("CustomerName", customer);
                        geofence.put("ProjectName", projectName);
                        geofence.put("ts", ts);
                        // adding geofences to geofences list
                        geofencesList.add(geofence);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
                ListAdapter adapter = new SimpleAdapter(
                        MyGeofences.this, geofencesList,
                        R.layout.list_item, new String[]{"LR", "ProjectName", "Latitude",
                        "Longitude"}, new int[]{R.id.lr,
                        R.id.projectName,
                        R.id.lat, R.id.lng});
                lv.setAdapter(adapter);

        }

    }
}

