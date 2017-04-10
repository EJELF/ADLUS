package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.edgarsjanovskis.adlus.model.MyActivities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class PostIntentService extends IntentService {
    private static final String TAG = PostIntentService.class.getSimpleName();

    public PostIntentService() {
        super(TAG);
    }

    public String myurl = "";
    private Integer phoneId = 0;
    private SharedPreferences prefs;
    String mGeofence;
    String mTrigger;
    MyActivities activity;

    @Override
    public void onCreate(){
        super.onCreate();// if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        // JUST DO IN BACKGROUND
        Toast.makeText(getBaseContext(), "Post Service started!", Toast.LENGTH_LONG).show();

        prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        phoneId = prefs.getInt("PhoneID", 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        System.out.println("POST INTENT RECEIVED");
        if (intent == null){
            //error
            Toast.makeText(getApplicationContext(), "No Intent received", Toast.LENGTH_LONG).show();
        }else{
            mGeofence = intent.getStringExtra("mGeofence");
            mTrigger = intent.getStringExtra("mTrigger");
        }
        if(isConnected())
            startPost();
    }

    private void startPost(){
        // call AsynTask to perform network operation on separate thread
        new PostIntentService.HttpAsyncTask().execute("http://"+myurl+"/api/Activities");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            activity = new MyActivities();
            activity.setmGeofence(mGeofence);
            activity.setmPhoneId(phoneId.toString());
            activity.setmTransition(mTrigger);
            activity.setmActivityTimestamp(new Date().toString());

            return POST(urls[0],activity);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "New data sent!", Toast.LENGTH_LONG).show();
        }
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @SuppressWarnings("deprecation")
    public String POST(String url, MyActivities actitity){
        InputStream inputStream;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("geofenceID", mGeofence);
            jsonObject.accumulate("phoneID", phoneId);
            jsonObject.accumulate("transitionStateID", mTrigger);
            //jsonObject.accumulate("transitionStateID", actitity.getmTransition());
            jsonObject.accumulate("dateTime", actitity.getmActivityTimestamp());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.i("InputStream", result);
                Integer statusCode = httpResponse.getStatusLine().getStatusCode();
                Log.i("Statuscode = ", statusCode.toString());
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result ="";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }
}
