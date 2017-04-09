package com.android.edgarsjanovskis.adlus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void btnStart_onClick (View view){
        Intent intent = new Intent(this, AppSettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
