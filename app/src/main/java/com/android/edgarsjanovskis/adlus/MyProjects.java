package com.android.edgarsjanovskis.adlus;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProjects extends AppCompatActivity {

    private String TAG = MyProjects.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // Jāizmanto statiskās komponentes, kuras tiks ievadītas no lietotāja puses!!!
    public String myurl = " ";
    private String myimei = " ";
    private String url;
    private SharedPreferences prefs;

    ArrayList<HashMap<String, String>> mProjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        myimei = prefs.getString("User_IMEI", " ");
        // URL to get contacts JSON
        url = "http://"+ myurl+ "/api/AndroidDbUpdates";
        mProjectList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        new GetProjects().execute();
    }



        /**
         * Async task class to get json by making HTTP call
         */
    private class GetProjects extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyProjects.this);
            pDialog.setMessage("Please wait...");
            //šādi uzstāda atsaukšanu
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {
                    if(pDialog.isShowing())
                        pDialog.dismiss();
                    finish();
                }
            });
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
                    //JSONObject jsonArray = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray geofences = new JSONArray(jsonStr);
                    // looping through All Array
                    for (int i = 0; i < geofences.length(); i++) {

                        JSONObject c = geofences.getJSONObject(i);

                        String id = c.getString("$id");
                        String geofenceId = c.getString("geofenceID");
                        String lr = c.getString("lr");
                        String lat = c.getString("latitude");
                        String lng = c.getString("longitude");
                        String radius = c.getString("radius");
                        String phoneId = c.getString("phoneID");
                        String imei = c.getString("imei");
                        String employee = c.getString("employeeName");
                        String customer = c.getString("customerName");
                        String projectName = c.getString("projectName");
                        String ts = c.getString("ts");
                        String custodianSurname = c.getString("custodianSurname");
                        String custodianPhone = c.getString("custodianPhone");

                        // tmp hash map for single contact
                        HashMap<String, String> geofence = new HashMap<>();
                        // adding each child node to HashMap key => value
                        geofence.put("$id", id);
                        geofence.put("GeofenceID", geofenceId);
                        geofence.put("LR", lr);
                        geofence.put("Latitude", lat);
                        geofence.put("Longitude", lng);
                        geofence.put("Radius", radius);
                        geofence.put("PhoneId", phoneId);
                        geofence.put("IMEI", imei);
                        geofence.put("EmployeeName", employee);
                        geofence.put("CustomerName", customer);
                        geofence.put("ProjectName", projectName);
                        geofence.put("ts", ts);
                        geofence.put("Custodian", custodianSurname);
                        geofence.put("CustodianPhone", custodianPhone);
                        // adding geofences to geofences list
                        mProjectList.add(geofence);
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
            }
            else {
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
                    MyProjects.this, mProjectList,
                    R.layout.list_item, new String[]{"LR", "ProjectName", "Latitude",
                    "Longitude", "IMEI"}, new int[]{R.id.lr,
                    R.id.projectName,
                    R.id.lat, R.id.lng, R.id.imei});

            lv.setAdapter(adapter);
            lv.setClickable(true); //aktivizē nospiešanu
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    //Toast.makeText(getApplicationContext(), "Nospiests: " + position, Toast.LENGTH_LONG).show();
                    /*
                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            MyProjects.this);
                    adb.setTitle("LR");
                    adb.setMessage(" selected Item is="
                            +lv.getItemAtPosition(position));
                    adb.setPositiveButton("Ok", null);
                    adb.show();*/
                    MoreInfoDialog dialog = new MoreInfoDialog();
                    FragmentManager fm = getFragmentManager();
                    dialog.show(fm, "moreInfo");

                }
            });
        }
    }
}

