package com.android.edgarsjanovskis.adlus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseControler dbControler;
    public static String uniqueID;

    public void createUUID() {
        uniqueID = UUID.randomUUID().toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createUUID();
        dbControler = new DatabaseControler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void butonShowMap_onClick(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);

    }

    public void buttonResponse_onClick (View view){
        Intent form = new Intent(this, GeofencesList.class);
        startActivity(form);
    }

    public void buttonUUID_onClick (View view){
        Intent intent = new Intent(this, PhoneInfoActivity.class);
        startActivity(intent);
    }
}
