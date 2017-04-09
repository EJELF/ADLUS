package com.android.edgarsjanovskis.adlus;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AppSettingsActivity extends AppCompatActivity {


    public final String USER_IMEI = "User_IMEI";
    public final String SERVER_URL = "Server_URL";
    public final String APP_START_TIME_HOUR = "startHour";
    public final String APP_START_TIME_MINUTE= "startMinute";
    public final String APP_STOP_TIME_HOUR = "stopHour";
    public final String APP_STOP_TIME_MINUTE= "stopMinute";
    public final String TIME_SELECTED = "timeSelected";
    private TextView tv1;
    private TextView tv2;
    private TimePicker tp1;
    private TimePicker tp2;
    private CheckBox cb;
    int hoursStart = 0;
    int minutesStart = 0;
    int hoursStop =0;
    int minutesStop = 0;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String imei = prefs.getString("User_IMEI", " ");
        final String server = prefs.getString("Server_URL", " ");
        EditText et1 = (EditText) findViewById(R.id.etImei);
        et1.setText(imei);
        EditText et2 = (EditText) findViewById(R.id.etServer);
        et2.setText(server);
        cb = (CheckBox)findViewById(R.id.cbTime);
        cb.setChecked(prefs.getBoolean("timeSelected", false));
        tp1 = (TimePicker) findViewById(R.id.tpStart);
        tv1 = (TextView)findViewById(R.id.tv1);
        tp2 = (TimePicker) findViewById(R.id.tpStop);
        tv2 = (TextView)findViewById(R.id.tv2);
        tp1.is24HourView();
        tp2.is24HourView();
        if(cb.isChecked()){
            tp1.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tp2.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
        }else
        {
            tp1.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            tp2.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);}

        if(Build.VERSION.SDK_INT < 23){
            tp1.setCurrentHour(prefs.getInt("startHour", 0));
            tp1.setCurrentMinute(prefs.getInt("startMinute", 0));
            tp2.setCurrentHour(prefs.getInt("stopHour", 0));
            tp2.setCurrentMinute(prefs.getInt("stopMinute", 0));
        }else {
            tp1.setHour(prefs.getInt("startHour", 0));
            tp1.setMinute(prefs.getInt("startMinute", 0));
            tp2.setHour(prefs.getInt("stopHour", 0));
            tp2.setMinute(prefs.getInt("stopMinute", 0));
        }
        //calendar = Calendar.getInstance();

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cb.isChecked()){
                    tv1.setVisibility(View.VISIBLE);
                    tp1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    tp2.setVisibility(View.VISIBLE);
                    setStartStopTime(tp1);
                    setStartStopTime(tp2);
                    SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
                    prefs.edit().putBoolean(TIME_SELECTED, true).apply();
                }else{
                    tv1.setVisibility(View.GONE);
                    tv2.setVisibility(View.GONE);
                    tp1.setVisibility(View.GONE);
                    tp2.setVisibility(View.GONE);
                    setStartStopTime(tp1);
                    setStartStopTime(tp2);
                    SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
                    prefs.edit().putBoolean(TIME_SELECTED, false).apply();
                }
            }
        });
    }
    @SuppressWarnings("deprecation")
    public void setStartStopTime(View view) {
        if (cb.isChecked()) {
            if (Build.VERSION.SDK_INT < 23) {
                hoursStart = tp1.getCurrentHour();
                minutesStart = tp1.getCurrentMinute();
            } else {
                hoursStart = tp1.getHour();
                minutesStart = tp1.getMinute();
            }
            }else {hoursStart = 0; minutesStart = 0;}
             /*   StringBuilder sb = new StringBuilder();
            sb.append(hoursStop).append(":");
            if (minutesStop<10){
                sb.append("0");
                sb.append(minutesStop);
            }
            else{sb.append(minutesStop);}
            String startTime = sb.toString();
        */
            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putInt(APP_START_TIME_HOUR, hoursStart).apply();
            prefs.edit().putInt(APP_START_TIME_MINUTE, minutesStart).apply();

        if (cb.isChecked()) {
            if(Build.VERSION.SDK_INT < 23){
                hoursStop = tp2.getCurrentHour();
                minutesStop = tp2.getCurrentMinute();
            } else{
                hoursStop = tp2.getHour();
                minutesStop= tp2.getMinute();
            }
        }else{hoursStop = 0; minutesStop = 0;}
        /*
        StringBuilder sb1 = new StringBuilder();
        sb1.append(hoursStop).append(":");
        if (minutesStop<10){
            sb.append("0");
            sb.append(minutesStop);
        }
        else{sb1.append(minutesStop);}
        String stopTime = sb1.toString();
*/
        SharedPreferences prefs1 = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        prefs1.edit().putInt(APP_STOP_TIME_HOUR, hoursStop).apply();
        prefs1.edit().putInt(APP_STOP_TIME_MINUTE, minutesStop).apply();
}

    public void buttonSave_onClick (View view){
        String userImei;
        String serverUrl;
        setStartStopTime(this.tp1);
        setStartStopTime(this.tp2);

        EditText et1 = (EditText)findViewById(R.id.etImei);
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

        userImei = et1.getText().toString().trim();

        if(userImei.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Lūdzu ievadiet tālruņa IMEI!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "LABI",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });

            builder1.setNegativeButton(
                    "ATLIKT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        else{

            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putString(USER_IMEI, userImei).apply();
        }

        serverUrl = et2.getText().toString().trim();

        if(serverUrl.equals("")) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Lūdzu ievadiet servera adresi un portu");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "LABI",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });

            builder1.setNegativeButton(
                    "ATLIKT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        else{
            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putString(SERVER_URL, serverUrl).apply();
        }
        // Create Brodcast if start/stop time is selected

        /*
        if (cb.isChecked()){
            Intent intent = new Intent();
            intent.putExtra("hoursStart", hoursStart);
            intent.putExtra("minutesStart", minutesStart);
            intent.putExtra("hoursStop", hoursStop);
            intent.putExtra("minutesStop", minutesStop);
            intent.setAction("com.android.edgarsjanovskis.adlus.TIME_BROADCAST");
            sendBroadcast(intent);

    }
   */



        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);

        String mStart;
        String mStop;
        if(minutesStart <10) {
            mStart = "0" + String.valueOf(minutesStart);
        }else {
            mStart = String.valueOf(minutesStart);
        }
        if(minutesStop <10) {
            mStop = "0" + String.valueOf(minutesStop);
        }else {
            mStop = String.valueOf(minutesStop);
        }
        Toast.makeText(getApplicationContext(),"AUTOMĀTISKA IESL.-IZSL. \n NO " + String.valueOf(hoursStart) + ":" + mStart + " LĪDZ "+ String.valueOf(hoursStop) + ":" + mStop,Toast.LENGTH_LONG).show();

        finish();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
}
