package com.wrongwaystudios.iou;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wrongwaystudios.iou.resources.Transaction;

public class DetailActivity extends AppCompatActivity {

    private Transaction lookingIOU = null;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lookingIOU = getIntent().getParcelableExtra("iouObject");
        isEditing = getIntent().getExtras().getBoolean("isEditing");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditing){
                    saveIOU();
                }
                else{
                    editIOU();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    protected void onStart(){

        super.onStart();

        if(lookingIOU == null){
            lookingIOU = getIntent().getParcelableExtra("iouObject");
        }

        // Grab each view and load information

        if(isEditing){
            editIOU();
        }
        else {
            displayInformation();
        }

    }

    private void saveIOU(){

    }

    private void editIOU(){

    }

    private void displayInformation(){

    }

}
