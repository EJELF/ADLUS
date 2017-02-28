package com.android.edgarsjanovskis.adlus;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class AppSettingsActivity extends AppCompatActivity {


    public final String USER_EMAIL = "User_email";
    public final String SERVER_URL = "Server_URL";
    public final String APP_START_TIME = "App_start_time";
    public final String APP_STOP_TIME = "App_stop_time";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        //Toast.makeText(this, "Šeit ievadīs iestatījumus", Toast.LENGTH_LONG).show();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //setupUI(findViewById(R.id.parent));
        SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
        String email = prefs.getString("User_email", " ");
        String server = prefs.getString("Server_URL", " ");
        EditText et1 = (EditText)findViewById(R.id.etEmail);
        et1.setText(email);
        EditText et2 = (EditText)findViewById(R.id.etServer);
        et2.setText(server);
    }

    /*
    public void setSettings(){


        CheckBox checkBox = (CheckBox)findViewById(R.id.cbTime);
        final TimePicker startTime = (TimePicker)findViewById(R.id.tpStart);
        startTime.setVisibility(View.INVISIBLE);
        final TimePicker stopTime = (TimePicker)findViewById(R.id.tpStop);
        stopTime.setVisibility(View.INVISIBLE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime.setVisibility(View.VISIBLE);
                    startTime.setIs24HourView(true);
                    startTime.setCurrentHour(8);
                    startTime.setCurrentMinute(0);
                    stopTime.setVisibility(View.VISIBLE);
                    stopTime.setIs24HourView(true);
                    stopTime.setCurrentHour(18);
                    stopTime.setCurrentMinute(0);
                    recreate();
                } else {return;}
            }
        });
    }
*/
    public void buttonSave_onClick (View view){

        String userEmail;
        String serverUrl;
        EditText et1 = (EditText)findViewById(R.id.etEmail);
        et1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        EditText et2 = (EditText)findViewById(R.id.etServer);
        et2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        userEmail = et1.getText().toString();

        if(USER_EMAIL != " ") {
            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putString(USER_EMAIL, userEmail).apply();
        }
        else{
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

        serverUrl = et2.getText().toString();

        if(SERVER_URL != " ") {
            SharedPreferences prefs = getSharedPreferences("AdlusPrefsFile", MODE_PRIVATE);
            prefs.edit().putString(SERVER_URL, serverUrl).apply();
        }
        else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Lūdzu ievadiet servera adresi un portu");
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
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
