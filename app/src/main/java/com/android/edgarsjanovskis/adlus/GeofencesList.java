package com.android.edgarsjanovskis.adlus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GeofencesList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofences_list);
    }

    @Override
    public void onResume(){
        super.onResume();

        int id, projectId, radius;
        float lat, lon;
        //String timestamp;
        ListView lv = (ListView)findViewById(R.id.list);

        final DatabaseControler dbContoler = new DatabaseControler(this);
        SQLiteDatabase dbReader = dbContoler.getReadableDatabase();
        String query = "SELECT * FROM "+ dbContoler.DATABASE_TABLE;
        Cursor reader = dbReader.rawQuery(query, null);
        ArrayList<String> list = new ArrayList<>();
        // ar if novērš kļūdu, kad android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0
        if(reader != null && reader.moveToFirst()) {
            while (reader.moveToNext()) ;
            {
                id = reader.getInt(reader.getColumnIndex("GeofenceId"));
                projectId = reader.getColumnIndex(dbContoler.PROJECT_ID_COLUMN);
                lat = reader.getColumnIndex(dbContoler.LATITUDE_COLUMN);
                lon = reader.getColumnIndex(dbContoler.LONGITUDE_COLUMN);
                //timestamp = String.valueOf(reader.getColumnIndex(dbContoler.TS_COLUMN));
                radius = reader.getColumnIndex(dbContoler.RADIUS_COLUMN);

                list.add(id + ":   " + projectId + " " + lat + "/" + lon + " Radius: " + radius);
            }
        }
        String[] data = new String[list.size()];
        for (int i = 0; i<list.size(); i++){
            data[i] = list.get(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
            lv.setAdapter(adapter);
        }

    }
}
