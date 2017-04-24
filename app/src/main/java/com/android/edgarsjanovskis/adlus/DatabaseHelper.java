package com.android.edgarsjanovskis.adlus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.edgarsjanovskis.adlus.model.MyGeofences;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class DatabaseHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "adlus.db";
    // Contacts table name
    public static final String TABLE_PROJECTS = "projects";
    public static final String TABLE_ACTIVITIES = "activities";
    // Projects Table Columns names
    //public static final String KEY_ID = "id";
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
    // Activities Table Columns names
    public static final String POST_ID = "PostId";
    public static final String STRING_JSON = "Json";

    //šī ideja no DobrinGanev
    //private static final String[] COLUMNS = {KEY_ID, LATITUDE_COLUMN, LONGITUDE_COLUMN, RADIUS_COLUMN};

     Data openHelper;
    private SQLiteDatabase database;
    Context context;
    //List<Geofence> mGeofenceList;
    ArrayList<Integer> newGeofenceIdList;


    public DatabaseHelper(Context context){
        openHelper = new  Data(context);
        database = openHelper.getWritableDatabase();
    }

    public void saveProjectsRecord(Integer geofenceId, String lr, Double lat, Double lng, Integer radius, Integer phoneId, String imei,
                                   String empl, String cust, String projname, String ts, String custod, String phone) {
        ContentValues contentValues = new ContentValues();
        //contentValues.put(KEY_ID, id);
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
        Cursor c = database.rawQuery("SELECT * FROM projects WHERE GeofenceId = " + geofenceId, null);

        newGeofenceIdList = new ArrayList<>();

        if (c.moveToFirst()) {
                database.update(TABLE_PROJECTS, contentValues, " GeofenceId = " + geofenceId, null);
                Log.e("Updated GeofenceId:", String.valueOf(geofenceId));
                newGeofenceIdList.add(geofenceId);
        } else {
                database.insert(TABLE_PROJECTS, null, contentValues);
                Log.e("Inserted GeofenceId: ", String.valueOf(geofenceId));
                newGeofenceIdList.add(geofenceId);
        }

        // jānodrošina izdzēšana, ja geofenceId vairāk nav json masīvā!!!
        //to nevar izdarīt šeit, jo cikls darbojas tikai uz json esošajiem objektiem
        if (c.isAfterLast()) {
            c.close();
            database.close();
        }
        //Log.println(Log.ERROR, "New records are: " , String.valueOf(newRecords));
        //getAllData();
    }


    public void saveActivityRecord (String Json){
        database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STRING_JSON, Json);
        database.insert("activities", null, contentValues);
        Log.e("Inserted content: ", contentValues.toString());
        database.close();
    }

    public Cursor getAllPendingJsonById(Integer _id){
        return database.rawQuery("SELECT * FROM activities WHERE PostId = " + _id, null);
    }


    public ArrayList<MyGeofences> getAllData(){

        ArrayList<MyGeofences> arrayList = new ArrayList<MyGeofences>();

        database = openHelper.getWritableDatabase();

        Cursor cc = database.rawQuery("SELECT *" + " FROM " + TABLE_PROJECTS, null);

        cc.moveToFirst();
        while(cc.isAfterLast() == false){

            MyGeofences geofenece = new MyGeofences(cc.getInt(cc.getColumnIndex(GEOFENCE_ID_COLUMN)),cc.getDouble(cc.getColumnIndex(LATITUDE_COLUMN)),cc.getDouble(cc.getColumnIndex(LONGITUDE_COLUMN)));

            geofenece.setTitle(cc.getString(cc.getColumnIndex(PROJECT_LR_COLUMN)));
            geofenece.setSnippet(cc.getString(cc.getColumnIndex(PROJECT_COLUMN)));
            geofenece.setmGeofenceId(Integer.parseInt(cc.getString(cc.getColumnIndex(GEOFENCE_ID_COLUMN))));
            arrayList.add(geofenece);

            cc.moveToNext();
        }
        database.close();
     return arrayList ;
    }

    public void showNewRecArray(){
        
            Integer[] arr = convert(newGeofenceIdList, Integer.class);

            for(int i=0; i<arr.length; i++) {
                Log.e("List items: ", String.valueOf(arr[i]));
            }
    }


    public static <T> T[] convert(ArrayList<T> newGeofenceIdList, Class clazz){
        return (T[]) newGeofenceIdList.toArray((T[]) Array.newInstance(clazz, newGeofenceIdList.size()));
    }

    public void deleteOldRecords() throws SQLiteException {
        Log.i("Executing delete", "Delete Started");
            database = openHelper.getWritableDatabase();
            //Cursor c = getAllRecordList();
            showNewRecArray();
            Cursor c = database.rawQuery("Select * from projects", null);

        if(c.moveToFirst())
                            while (!c.isAfterLast()) {
                                if (!newGeofenceIdList.contains(c.getInt(0))) {
                                Log.e("Current id", String.valueOf(c.getInt(0)));
                                database.delete(TABLE_PROJECTS, GEOFENCE_ID_COLUMN + " =? ", new String[]{String.valueOf(c.getInt(0))});
                                Log.e("Deleted GeofenceId: ", String.valueOf(c.getInt(0)));
                                c.moveToNext();
                            }
            }
            if (c.isAfterLast()){
                c.close();
                //newGeofenceIdList = null;
            }
        database.close();
    }

/*
    public boolean checkProjects(int id){
        database = openHelper.getReadableDatabase();

        Cursor c = database.query(TABLE_PROJECTS, new String[]{GEOFENCE_ID_COLUMN}, GEOFENCE_ID_COLUMN + " = ? ",
                new String[] {String.valueOf(id)},
                null, null, null, null);

        if (c.moveToFirst()) {
            return true;
        } // row exist
            else{
            return false;
        }
    }
*/

    public Cursor getAllRecordList(){
        return database.rawQuery("select * from " + TABLE_PROJECTS, null);
    }

    private class Data extends SQLiteOpenHelper {
        public  Data(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_PROJECTS + "("
                    //+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GEOFENCE_ID_COLUMN + " INTEGER PRIMARY KEY, " + PROJECT_LR_COLUMN + " Text, " + LATITUDE_COLUMN + " Float, "
                    + LONGITUDE_COLUMN + " Float, " + RADIUS_COLUMN + " Integer, " + PHONE_ID_COLUMN + " Integer, "
                    + IMEI_COLUMN + " Text, " + EMPLOYEE_COLUMN + " Text, " + CUSTOMER_COLUMN + " Text, " + PROJECT_COLUMN + " Text, "
                    + TS_COLUMN + " Text, " + CUSTODIAN_COLUMN + " Text, "
                    + CUSTODIAN_PHONE_COLUMN + " Text" + ")");
            Log.e("Tabula - ", TABLE_PROJECTS + " - izveidota");
            //////////////////

            db.execSQL("CREATE TABLE " + TABLE_ACTIVITIES + "("
                    + POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STRING_JSON +  " STRING" + ")");
            Log.e("Tabula - ", TABLE_ACTIVITIES + " - izveidota");

            ///////////////////
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
            db.execSQL("DROP TABLE IF EXIST " + TABLE_ACTIVITIES);
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
