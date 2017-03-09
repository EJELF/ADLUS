package com.android.edgarsjanovskis.adlus;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ProjectsHelper{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    public SharedPreferences prefs;

    // Database Name
    private static final String DATABASE_NAME = "adlus.db";
    // Contacts table name
    public static final String TABLE_PROJECTS = "projects";
    // Projects Table Columns names
    public static final String KEY_ID = "id";
    public static final String GEOFENCE_ID_COLUMN = "GeofenceId";
    public static final String PROJECT_LR_COLUMN = "ProjectLr";
    public static final String LATITUDE_COLUMN = "Latitude";
    public static final String LONGITUDE_COLUMN = "Longitude";
    public static final String RADIUS_COLUMN = "Radius";
    public static final String PHONE_ID_COLUMN = "PhoneId";
    public static final String IMEI_COLUMN = "Imei";
    public static final String EMPLOYEE_COLUMN = "EmployeeName";
    public static final String CUSTOMER_COLUMN = "CustomerName";
    public static final String PROJECT_COLUMN = "ProjectName";
    public static final String TS_COLUMN = "ts";
    public static final String CUSTODIAN_COLUMN = "CustodianSurname";
    public static final String CUSTODIAN_PHONE_COLUMN = "CustodianPhone";

    Projects openHelper;
    private SQLiteDatabase database;
    Context context;


    public ProjectsHelper(Context context){
        openHelper = new Projects(context);
        database = openHelper.getWritableDatabase();
    }

    public void saveProjectsRecord(String id, String geofenceId, String lr, String lat, String lng, String radius, String phoneId, String imei,
                                   String empl, String cust, String projname, String ts, String custod, String phone){
        ContentValues contentValues =new ContentValues();
        contentValues.put(KEY_ID, id);
        contentValues.put(GEOFENCE_ID_COLUMN, geofenceId);
        contentValues.put(PROJECT_LR_COLUMN, lr);
        contentValues.put(LATITUDE_COLUMN, lat);
        contentValues.put(LONGITUDE_COLUMN, lng);
        contentValues.put(RADIUS_COLUMN, radius);
        contentValues.put(PHONE_ID_COLUMN, phoneId);
        contentValues.put(IMEI_COLUMN, imei);
        contentValues.put(EMPLOYEE_COLUMN, empl);
        contentValues.put(CUSTOMER_COLUMN, cust);
        contentValues.put(PROJECT_COLUMN, projname);
        contentValues.put(TS_COLUMN, ts);
        contentValues.put(CUSTODIAN_COLUMN, custod);
        contentValues.put(CUSTODIAN_PHONE_COLUMN, phone);
        //contentValues.put(LAST_DB_UPDATE, " time('now') ");


        Cursor c = database.rawQuery("SELECT * FROM projects WHERE GeofenceId='" + geofenceId +"'", null);
        if(c.moveToFirst())
        {
            //showMessage("Error", "Record exist");
            Log.e("record exist id: ", id + " GeofenceId: " + geofenceId);
        }
        else
        {
            // Inserting record
            database.insert(TABLE_PROJECTS, null, contentValues);

            if(c.isAfterLast()) {
                c.close();
            }
        }
       //database.close();    met ārā, ka cenšas atvērt aizvērtu db
        ////////////prefs = context.getApplicationContext().getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
    }


    public Cursor getTimeRecordList(){
        return database.rawQuery("select * from " + TABLE_PROJECTS, null);

    }

    private class Projects extends SQLiteOpenHelper{
        public Projects(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE " + TABLE_PROJECTS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GEOFENCE_ID_COLUMN + " Integer, " + PROJECT_LR_COLUMN + " Text, " + LATITUDE_COLUMN + " Float, "
                    + LONGITUDE_COLUMN + " Float, " + RADIUS_COLUMN + " Integer, " + PHONE_ID_COLUMN + " Integer, "
                    + IMEI_COLUMN + " Text, " + EMPLOYEE_COLUMN + " Text, " + CUSTOMER_COLUMN + " Text, " + PROJECT_COLUMN + " Text, "
                    + TS_COLUMN + " Integer, " + CUSTODIAN_COLUMN + " Text, "
                    + CUSTODIAN_PHONE_COLUMN + " Text" +")");
            Log.e("Tabula - ", TABLE_PROJECTS + " - izveidota");
        }
        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
            // Create tables again
            onCreate(db);
        }

    }
}
