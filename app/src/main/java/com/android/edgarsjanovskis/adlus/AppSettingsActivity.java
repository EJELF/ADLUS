package com.android.edgarsjanovskis.adlus;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


public class AppSettingsActivity extends AppCompatActivity {


    public final String USER_EMAIL = "User_email";
    public final String SERVER_URL = "Server_URL";
    public final String APP_START_TIME = "App_start_time";
    public final String APP_STOP_TIME = "App_stop_time";
    private Calendar calendar;
    private TimePicker tp1;
    private TimePicker tp2;
    private String startTime;
    private String stopTime;
    private CheckBox cb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        //Toast.makeText(this, "Šeit ievadīs iestatījumus", Toast.LENGTH_LONG).show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //setupUI(findViewById(R.id.parent));
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String email = prefs.getString("User_email", " ");
        String server = prefs.getString("Server_URL", " ");
        String startTime = prefs.getString("App_start_time", " ");
        String stopTime = prefs.getString("App_stop_time", " ");
        EditText et1 = (EditText) findViewById(R.id.etEmail);
        et1.setText(email);
        EditText et2 = (EditText) findViewById(R.id.etServer);
        et2.setText(server);

        tp1 = (TimePicker) findViewById(R.id.tpStart);
        tp2 = (TimePicker) findViewById(R.id.tpStop);
        calendar = Calendar.getInstance();

        int hoursStart = calendar.get(Calendar.HOUR_OF_DAY);
        int minutesStart = calendar.get(Calendar.MINUTE);
        showStartTime(hoursStart, minutesStart);

        int hoursStop = calendar.get(Calendar.HOUR_OF_DAY);
        int minutesStop = calendar.get(Calendar.MINUTE);
        showStartTime(hoursStop, minutesStop);
    }

    public void setStartTime(View view){
        if(Build.VERSION.SDK_INT < 23){
            int hoursStart = tp1.getCurrentHour();
            int minutesStart = tp1.getCurrentMinute();
            showStartTime(hoursStart, minutesStart);

        } else{
            int hoursStart = tp1.getHour();
            int minutesStart = tp1.getMinute();
            showStartTime(hoursStart, minutesStart);
        }

    }
    public void setStopTime(View view){
        if(Build.VERSION.SDK_INT < 23){
            int hoursStop = tp2.getCurrentHour();
            int minutesStop = tp2.getCurrentMinute();
            showStopTime(hoursStop, minutesStop);
        } else{
            int hoursStop = tp2.getHour();
            int minutesStop= tp2.getMinute();
            showStopTime(hoursStop, minutesStop);
        }

    }

        public void showStartTime(int hoursStart, int minutesStart){
            StringBuilder sb1 = new StringBuilder();
            sb1.append(hoursStart).append(" : ").append(minutesStart);
            startTime = sb1.toString();
        }
        public void showStopTime(int hoursStop, int minutesStop){
            StringBuilder sb2 = new StringBuilder();
            sb2.append(hoursStop).append(" : ").append(minutesStop);
            stopTime = sb2.toString();
        }


    public void buttonSave_onClick (View view){
        String userEmail;
        String serverUrl;

        //if (cb.isChecked()) {
            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putString(APP_START_TIME, startTime).apply();
            SharedPreferences prefs1 = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs1.edit().putString(APP_STOP_TIME, stopTime).apply();
        //} else {return;}

        EditText et1 = (EditText)findViewById(R.id.etEmail);
        et1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        });
        EditText et2 = (EditText)findViewById(R.id.etServer);
        et2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        });

        userEmail = et1.getText().toString();
        if(userEmail.equals(" ")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Lūdzu ievadiet savu e-pastu!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "LABI",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "ATLIKT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else{

            SharedPreferences prefs3 = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs3.edit().putString(USER_EMAIL, userEmail).apply();
        }

        serverUrl = et2.getText().toString();

        if(serverUrl.equals(" ")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Lūdzu ievadiet servera adresi un portu");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "LABI",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "ATLIKT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else{
            SharedPreferences prefs4 = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs4.edit().putString(SERVER_URL, serverUrl).apply();
        }
    }

        public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

}
