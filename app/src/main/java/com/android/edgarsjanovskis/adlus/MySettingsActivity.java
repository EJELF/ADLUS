package com.android.edgarsjanovskis.adlus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MySettingsActivity extends AppCompatActivity {

    EditText txtEmail = (EditText) findViewById(R.id.etEmail);
    Button btnSave = (Button)findViewById(R.id.btnSave);
    // set current version code to 0 if settings are not filled properly
    //int currentVersionCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);

        Toast.makeText(getApplicationContext(), "Šeit ievadīs settingus", Toast.LENGTH_LONG).show();
    }




    public void btnSave_onClick (View view){
       //currentVersionCode = 1;

    }


}