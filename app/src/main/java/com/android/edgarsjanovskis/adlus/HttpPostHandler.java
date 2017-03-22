package com.android.edgarsjanovskis.adlus;


//Created by Edgars on 23.12.16.

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpPostHandler {
    private static final String TAG = HttpPostHandler.class.getSimpleName();

    HttpPostHandler() {}

    String makeServiceCall(String reqUrl) {
        String message = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            int status = conn.getResponseCode();

            // read the response
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            //message = convertStreamToString(out);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return message;
    }
}
