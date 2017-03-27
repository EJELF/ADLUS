package com.android.edgarsjanovskis.adlus;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Edgars on 19.03.17.
 */

public class PostActivity extends AppCompatActivity{

    TextView tvIsConnected;
    EditText etGeofence,etPhone,etTransition, etDateTime;
    Button btnPost;
    public String myurl = "";
    private String myimei = "";
    private Integer phoneId = 0;
    private String url;
    private SharedPreferences prefs;

    MyActivities activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etGeofence = (EditText) findViewById(R.id.etGeofence);
        //etPhone = (EditText) findViewById(R.id.etPhone);
        etTransition = (EditText) findViewById(R.id.etTransition);
        //etDateTime = (EditText) findViewById(R.id.etDateTime);
        btnPost = (Button) findViewById(R.id.btnPost);
        prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        myurl = prefs.getString("Server_URL", " ");
        Log.i("URL: ", myurl);
        phoneId = prefs.getInt("PhoneID", 0);
        // URL to get contacts JSON
        //url = "http://"+myurl+"/api/Activities";

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }

        // add click listener to Button "POST"
       // btnPost.setOnClickListener((View.OnClickListener) this);

    }

    public String POST(String url, MyActivities actitity){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("geofenceID", actitity.getmGeofence());
            jsonObject.accumulate("phoneID", phoneId);
            jsonObject.accumulate("transitionStateID", actitity.getmTransition());
            jsonObject.accumulate("dateTime", actitity.getmActivityTimestamp());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void buttonPost_onClick(View view) {

        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://"+myurl+"/api/Activities");
                break;
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            activity = new MyActivities();
            activity.setmGeofence(etGeofence.getText().toString());
            activity.setmPhoneId(phoneId.toString());
            activity.setmTransition(etTransition.getText().toString());
            activity.setmActivityTimestamp(new Date().toString());

            return POST(urls[0],activity);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "New data sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
        if(etGeofence.getText().toString().trim().equals(""))
            return false;
        //else if(etPhone.getText().toString().trim().equals(""))
        //    return false;
        else if(etTransition.getText().toString().trim().equals(""))
            return false;
       // else if(etDateTime.getText().toString().trim().equals(""))
        //    return false;
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }
}




