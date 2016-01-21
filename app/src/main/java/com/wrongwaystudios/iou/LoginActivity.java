package com.wrongwaystudios.iou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        else if (getActionBar() != null){
            getActionBar().hide();
        }

        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onStart(){

        super.onStart();

    }

}
