package com.android.edgarsjanovskis.adlus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Timestamp;
import java.util.ArrayList;

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


public class DbList extends AppCompatActivity {

    // No Parauga!!!!!!!
    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    //public static List<Geofence> mGeofenceList1;
    //public static List<LatLng> mLatLonList1;
    // Persistent storage for geofences.


    // These will store hard-coded geofences in this sample app.
    Cursor reader;
    ProjectsHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        //mGeofenceList1 = new ArrayList<>();
        //mLatLonList1 = new ArrayList<>();
        //Intent intent = new Intent(this, GeofencingActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();

        int id, phoneId, geofenceId;
        float radius;
        double lat;
        double lon;
        Timestamp lastUpdate;
        String projectLr, timestamp, employee, customer, project, imei, custodian, custodianPhone;
        ListView lv = (ListView)findViewById(list);

        mDbHelper = new ProjectsHelper(this);
        reader = mDbHelper.getAllRecordList();

        ArrayList<String> sqlList = new ArrayList<>();


        // ar if novērš kļūdu, kad android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        if(reader != null)
            try{
        for( reader.moveToFirst(); !reader.isAfterLast(); reader.moveToNext() ) {
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

            /*///////////////////////////////////////////////////////////// pamēģināšu šeit
            LatLng latLng = new LatLng(lat, lon);
            Geofence fence= new Geofence.Builder()
                    .setRequestId(String.valueOf(geofenceId))
                    .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                    .setExpirationDuration( GEOFENCE_EXPIRATION_TIME )
                    .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT )
                    .build();
            mGeofenceList1.add(fence);
            mLatLonList1.add(latLng);
            Log.e("izveidots ", fence.toString());
            /////////////////////////////////////////////////////////////*/

            sqlList.add("Geofence ID: " + geofenceId + "\nLatitude: " + lat + "\nLongitude: " + lon + "\nRadius: " + radius);
            }
        }catch (Exception e) {
                Log.e("Error: ", e.toString());

            }finally {
            if (reader.isAfterLast()){
                reader.close();
            }
        }

        String[] data = new String[sqlList.size()];
        for (int i = 0; i< sqlList.size(); i++){
            data[i] = sqlList.get(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, data);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(reader != null)
            reader.close();
    }


}
