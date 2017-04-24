package com.android.edgarsjanovskis.adlus;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.edgarsjanovskis.adlus.model.MyGeofences;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    private PendingIntent mPostPendingIntent;

    public GeofenceTrasitionService() {
        super(TAG);
    }

    Context mContext;
    MyGeofences myGeofences;
    String mSnippet;
    Intent postIntent;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("GEOFENCING INTENT RECEIVED");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if (geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e( TAG, errorMsg );
            return;
        }

        //
        Location location  = geofencingEvent.getTriggeringLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();
        int transitionId = geofencingEvent.getGeofenceTransition();

        Log.e(TAG, "FromIntent lat/lng: " + latitude+"/"+longitude + " TransitionID:" + transitionId+ " Accuracy:"+ accuracy  + "LR :" + lr);
        //

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        //String geofenceName = geofencingEvent.getTriggeringGeofences().get(0).toString();
        Log.e(TAG, "Geofence transition: " + geoFenceTransition);

        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);

            // Send notification details as a String
            sendNotification(geofenceTransitionDetails);

            // THIS SHOULD SEND EXTRAS TO POST ACTIVITY
            startPostIntent(geoFenceTransition, triggeringGeofences.get(0));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }

    private Integer triggeringGeofenceId;
    private String lr = "LR...";
    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {

        //get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
            triggeringGeofenceId = Integer.parseInt(geofence.getRequestId());
        }

        String status = null;
            if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                status = "Esi reģistrēts ";
            else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                status = "Izreģistrējies no ";
            return status + triggeringGeofenceId + " (" + lr + ")";//TextUtils.join( ", ", triggeringGeofences);
    }

    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );
        // Intent to start the main Activity
        Intent notificationIntent = Main2Activity.makeNotificationIntent(getApplicationContext(), msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main2Activity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificationMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );
        notificationMng.notify(GEOFENCE_NOTIFICATION_ID, createNotification(msg, notificationPendingIntent));
    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        NotificationCompat.Builder builder = notificationBuilder
                .setSmallIcon(R.drawable.ic_action_location)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("ADLUS paziņojums!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private void startPostIntent (int geoFenceTransition, Geofence geofence){
        postIntent = new Intent(mContext, PostVolleyIntentService.class);
        postIntent.putExtra("mGeofence", geofence.getRequestId());
        postIntent.putExtra("mTrigger", String.valueOf(geoFenceTransition));
        Log.e(TAG, "Extras sent " + geofence.getRequestId() + ", " + geoFenceTransition);
        startService(postIntent);
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    private String getLrFromId(int id){
        myGeofences = new MyGeofences(id);
        myGeofences.getSnippet();
        return mSnippet;
    }

}
