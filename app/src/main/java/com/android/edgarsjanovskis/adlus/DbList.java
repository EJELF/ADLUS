package com.android.edgarsjanovskis.adlus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.android.edgarsjanovskis.adlus.DatabaseHelper.GEOFENCE_ID_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.LATITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.LONGITUDE_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.PROJECT_LR_COLUMN;
import static com.android.edgarsjanovskis.adlus.DatabaseHelper.RADIUS_COLUMN;
import static com.android.edgarsjanovskis.adlus.R.id.list;

// This class is used only for debugging purposes!!!
public class DbList extends AppCompatActivity {

    Cursor reader;
    DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
    }
    @Override
    public void onResume(){
        super.onResume();

        int geofenceId;
        String lr;
        float radius;
        double lat;
        double lon;

        ListView lv = (ListView)findViewById(list);
        ArrayList<String> sqlList = new ArrayList<>();

        mDbHelper = new DatabaseHelper(this);
        reader = mDbHelper.getAllRecordList();

        // ar if novērš kļūdu, kad android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        if(reader != null)
            try{
        for( reader.moveToFirst(); !reader.isAfterLast(); reader.moveToNext() ) {
            geofenceId = reader.getInt(reader.getColumnIndex(GEOFENCE_ID_COLUMN));
            lr = reader.getString(reader.getColumnIndex(PROJECT_LR_COLUMN));
            lat = reader.getDouble(reader.getColumnIndex(LATITUDE_COLUMN));
            lon = reader.getDouble(reader.getColumnIndex(LONGITUDE_COLUMN));
            radius = reader.getFloat(reader.getColumnIndex(RADIUS_COLUMN));

            sqlList.add("Geofence ID: " + geofenceId +  " ("+ lr + ") "+ "\nLatitude: " + lat + "\nLongitude: " + lon + "\nRadius: " + radius);
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
