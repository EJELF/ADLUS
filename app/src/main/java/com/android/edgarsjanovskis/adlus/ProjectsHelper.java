package com.android.edgarsjanovskis.adlus;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.List;


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
    //šī ideja no DobrinGanev
    private static final String[] COLUMNS = {KEY_ID, LATITUDE_COLUMN, LONGITUDE_COLUMN, RADIUS_COLUMN};

    Projects openHelper;
    private SQLiteDatabase database;
    Context context;
    List<Geofence> mGeofenceList;
    //int oldVersion = DATABASE_VERSION;
    //int newVersion = oldVersion;


    public ProjectsHelper(Context context){
        openHelper = new Projects(context);
        database = openHelper.getWritableDatabase();
        database = openHelper.getReadableDatabase();

    }

    public void saveProjectsRecord(String id, String geofenceId, String lr, String lat, String lng, String radius, String phoneId, String imei,
                                   String empl, String cust, String projname, String ts, String custod, String phone) {
        ContentValues contentValues = new ContentValues();
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

        database = openHelper.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM projects WHERE GeofenceId=" + geofenceId, null);

        if (c.moveToFirst()) {
            //database.update(TABLE_PROJECTS, contentValues, "id=? ", null );
            Log.e("record exist id: ", id + " GeofenceId: " + geofenceId);
        }
        else {
            //newVersion =+1 ;
            //database.setVersion(newVersion);
            database.insert(TABLE_PROJECTS, null, contentValues);
            //Log.e("DB upgraded to ver.: ", String.valueOf(newVersion));

        }
        if (c.isAfterLast()) {
            c.close();
            database.close();
        }
    }
            //database.close();    //met ārā, ka cenšas atvērt aizvērtu db



    public Cursor getAllRecordList(){
        return database.rawQuery("select * from " + TABLE_PROJECTS, null);
    }

    private class Projects extends SQLiteOpenHelper {
        public Projects(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_PROJECTS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GEOFENCE_ID_COLUMN + " Integer, " + PROJECT_LR_COLUMN + " Text, " + LATITUDE_COLUMN + " Float, "
                    + LONGITUDE_COLUMN + " Float, " + RADIUS_COLUMN + " Integer, " + PHONE_ID_COLUMN + " Integer, "
                    + IMEI_COLUMN + " Text, " + EMPLOYEE_COLUMN + " Text, " + CUSTOMER_COLUMN + " Text, " + PROJECT_COLUMN + " Text, "
                    + TS_COLUMN + " Integer, " + CUSTODIAN_COLUMN + " Text, "
                    + CUSTODIAN_PHONE_COLUMN + " Text" + ")");
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
        /*
        @Override
        public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
            // Create tables again
            onCreate(db);
        }
        */


        //??? Kā dabūt no datubāzes@@@
/*
        public void createGeofence(int id){
            //1. get reference to readable db
            SQLiteDatabase db = this.getReadableDatabase();
            //2. buld quiry

            Cursor c = db.query(TABLE_PROJECTS, // a. table
                    COLUMNS,// b. column names
                    " id = ?", // c. selections
                    new String[] {String.valueOf(id)}, // d. selections args
                    null,// e. group by
                    null,// f. having
                    null,// g. order by
                    null);// h. limit
            // 3. if we got results get the first one
            if (c != null)
                c.moveToFirst();
            // 4. build book object
            LatLng latLng = new LatLng((c.getFloat(1)), (c.getFloat(2)));
            Geofence fence= new Geofence.Builder()
                    .setRequestId(c.getString(0))
                    .setCircularRegion( latLng.latitude, latLng.longitude, c.getLong(3))
                    .setExpirationDuration( GEOFENCE_EXPIRATION_TIME )
                    .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT )
                    .build();
            mGeofenceList.add(fence);
        }
        public void createGeofence(){
            //1. get reference to readable db
            SQLiteDatabase db = this.getReadableDatabase();
            //2. buld quiry

            Cursor c = db.query(TABLE_PROJECTS, // a. table
                    COLUMNS,// b. column names
                    " id = ?", // c. selections
                    new String[] {String.valueOf(id)}, // d. selections args
                    null,// e. group by
                    null,// f. having
                    null,// g. order by
                    null);// h. limit
            // 3. if we got results get the first one
            if (c != null)
                c.moveToFirst();
            // 4. build book object
            LatLng latLng = new LatLng((c.getFloat(1)), (c.getFloat(2)));
            Geofence fence= new Geofence.Builder()
                    .setRequestId(c.getString(0))
                    .setCircularRegion( latLng.latitude, latLng.longitude, c.getLong(3))
                    .setExpirationDuration( GEOFENCE_EXPIRATION_TIME )
                    .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT )
                    .build();
            mGeofenceList.add(fence);
        }*/


    }

}
