package com.android.edgarsjanovskis.adlus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.edgarsjanovskis.adlus.model.MyGeofences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.android.edgarsjanovskis.adlus.Main2Activity.LAST_UPDATE;


public class GetMyProjects extends AppCompatActivity {

    private String TAG = GetMyProjects.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    // User entered components as IMEI and URL!!!
    public String myurl = " ";
    private String myimei = " ";
    private String url;
    private SharedPreferences prefs;
    public final String USER_NAME = "User_Name";
    public final String PHONE_ID = "PhoneID";
    public ArrayList<HashMap<String, String>> mProjectList;
    // add a ProjectHelper to Activity (protected???)
    protected DatabaseHelper databaseHelper;
    ArrayList<Integer> newRecords;
    MyGeofences geofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        //start new instance of DatabaseHelper and reader
        databaseHelper = new DatabaseHelper(this);
        //Cursor reader = databaseHelper.getTimeRecordList();
        prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        myimei = prefs.getString("User_IMEI", " ");
        // URL to get contacts JSON
        url = "http://"+myurl+"/api/AndroidDbUpdates";
        mProjectList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        newRecords = new ArrayList<>();
        //if(Main2Activity.isConnected){
            new GetProjects().execute();
        //}else{
            Toast.makeText(getApplicationContext(), "Lai saņemtu datus, Tev jābūt piekļuvei \n   internetam un aktīvizētam VPN ", Toast.LENGTH_LONG).show();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pDialog.isShowing())
            pDialog.dismiss();

    }

        /**
         * Async task class to get json by making HTTP call
         */
    private class GetProjects extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GetMyProjects.this);
            pDialog.setMessage("Lūdzu uzgaidi! ADLUS saņem datus no servera...");
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
            HttpGetHandler sh = new HttpGetHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonArray = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray projects = new JSONArray(jsonStr);
                    SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);

                    // looping through All Array
                    for (int i = 0; i < projects.length(); i++) {

                        JSONObject c = projects.getJSONObject(i);

                        String id = c.getString("$id");
                        Integer geofenceId = c.getInt("geofenceID");
                        String lr = c.getString("lr");
                        Double lat = c.getDouble("latitude");
                        Double lng = c.getDouble("longitude");
                        Integer radius = c.getInt("radius");
                        Integer phoneId = c.getInt("phoneID");
                        String imei = c.getString("imei");
                        String employee = c.getString("employeeName");
                        String customer = c.getString("customerName");
                        String projectName = c.getString("projectName");
                        String ts = c.getString("ts");
                        String custodianSurname = c.getString("custodianSurname");
                        String custodianPhone = c.getString("custodianPhone");


                        if (myimei.equals(imei)) {
                            // tmp hash map for single contact
                            HashMap<String, String> project = new HashMap<>();
                            // adding each child node to HashMap key => value
                            project.put("$id", id);
                            project.put("GeofenceID", String.valueOf(geofenceId));
                            project.put("LR", lr);
                            project.put("Latitude", String.valueOf(lat));
                            project.put("Longitude", String.valueOf(lng));
                            project.put("Radius", String.valueOf(radius));
                            project.put("PhoneId", String.valueOf(phoneId));
                            project.put("IMEI", imei);
                            project.put("EmployeeName", employee);
                            project.put("CustomerName", customer);
                            project.put("ProjectName", projectName);
                            project.put("ts", ts);
                            project.put("Custodian", custodianSurname);
                            project.put("CustodianPhone", custodianPhone);
                            // adding projects to project list
                            mProjectList.add(project);
                            geofence = new MyGeofences(lat, lng, lr, projectName);
                            geofence.setmGeofenceId(geofenceId);
                            newRecords.add(geofenceId);
                            Log.i(TAG, "Geofence object created :" + geofence.getTitle());

                            //db storing only if IMEI match
                            databaseHelper.saveProjectsRecord(geofenceId, lr, lat, lng, radius, phoneId, imei, employee, customer, projectName, ts, custodianSurname, custodianPhone);

                            prefs.edit().putString(USER_NAME, employee).apply();
                            prefs.edit().putInt(PHONE_ID, phoneId).apply();
                            // place where last update
                            String updateDatetime = String.valueOf(Calendar.getInstance().getTime());
                            prefs.edit().putString(LAST_UPDATE, updateDatetime).apply();
                        }
                    }
                    //databaseHelper.deleteOldRecords();

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
                                "Ups, nevarēja saņemt datus no servea. Pārbaudi ADLUS iestatījumus un mēģini vēlreiz!",
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

             //Updating parsed JSON data into ListView

            ListAdapter adapter = new SimpleAdapter(
                    GetMyProjects.this, mProjectList,
                    R.layout.list_item, new String[]{"LR", "ProjectName"
            }, new int[]{R.id.lr,
                    R.id.projectName
            });

            lv.setAdapter(adapter);
            lv.setClickable(true); //aktivizē nospiešanu

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                    // nezinu ko lai dara!!!
                    StringBuilder sb = new StringBuilder();
                    StringBuilder phone = sb.append(mProjectList.get(i).get("CustodianPhone"));

                    //Toast.makeText(getApplicationContext(), "Nospiests: " + position, Toast.LENGTH_LONG).show();
                    AlertDialog.Builder adb = new AlertDialog.Builder(GetMyProjects.this);
                    view.setSelected(true);
                    adb.setTitle("Objekta ID:  " + mProjectList.get(i).get("LR"));
                    adb.setMessage("Objekta nosaukums:  "+ '"'   + mProjectList.get(i).get("ProjectName") + '"' +
                            "\n\nObjekta koordinātes: " + mProjectList.get(i).get("Latitude")+
                            "/" + mProjectList.get(i).get("Longitude") +
                            "\n\nBūvuzraugs: " + mProjectList.get(i).get("Custodian") +
                            "\nTālrunis:  " + phone
                    );

                    adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    adb.setPositiveButton(R.string.show_map, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(),ProjectOnMapActivity.class);
                            String lat = mProjectList.get(i).get("Latitude");
                            String lng = mProjectList.get(i).get("Longitude");
                            String lr = mProjectList.get(i).get("LR");
                            String radius = mProjectList.get(i).get("Radius");
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            intent.putExtra("lr", lr);
                            intent.putExtra("radius", radius);
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
                    adb.show();
                }
            });
        }
    }

}

