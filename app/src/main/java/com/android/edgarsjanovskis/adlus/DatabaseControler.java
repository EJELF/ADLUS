package com.android.edgarsjanovskis.adlus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Edgars on 09.12.16.
 */

public class DatabaseControler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "adlusDB.db";
    private static final int DATEBASE_VERSION = 2;
    public static final String DATABASE_TABLE = "Geofences";
    //protected static final String GEOFENCE_ID_COLUMN = "GeofenceId";
    public static final String PROJECT_ID_COLUMN = "ProjectId";
    public static final String LATITUDE_COLUMN = "Latitude";
    public static final String LONGITUDE_COLUMN = "Longitude";
    //public static final String TS_COLUMN = "ts";
    public static final String RADIUS_COLUMN= "Radius";

    public DatabaseControler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableRequest = "CREATE TABLE " + DATABASE_TABLE + " ( GeofenceId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PROJECT_ID_COLUMN + " Integer, " + LATITUDE_COLUMN + " Float, " + LONGITUDE_COLUMN + " Float, "
                + RADIUS_COLUMN + " Integer); ";
        db.execSQL(createTableRequest);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    DatabaseControler (Context context){
        super(context, DATABASE_NAME, null, DATEBASE_VERSION);
    }
}

