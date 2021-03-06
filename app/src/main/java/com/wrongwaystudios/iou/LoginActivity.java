package com.wrongwaystudios.iou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wrongwaystudios.iou.resources.Globals;
import com.wrongwaystudios.iou.resources.OAuthObject;
import com.wrongwaystudios.iou.resources.User;
import com.wrongwaystudios.iou.resources.ViewHelper;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {

    private FancyButton signInBtn = null;
    private FancyButton signUpBtn = null;
    private FancyButton cancelBtn = null;
    private MaterialEditText usernameEdit = null;
    private MaterialEditText passwordEdit = null;
    private MaterialEditText firstNameEdit = null;
    private MaterialEditText lastNameEdit = null;
    private MaterialEditText rePasswordEdit = null;
    private AnimatedCircleLoadingView animatedCircleLoadingView = null;

    private Context signInContext;

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

        signInContext = this;
        Globals.authObject = new OAuthObject(this);

        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onStart(){

        super.onStart();

        // Load each view
        signInBtn = (FancyButton) findViewById(R.id.btn_sign_in);
        signUpBtn = (FancyButton) findViewById(R.id.btn_sign_up);
        cancelBtn = (FancyButton) findViewById(R.id.btn_cancel_sign_up);
        usernameEdit = (MaterialEditText) findViewById(R.id.username_edit);
        passwordEdit = (MaterialEditText) findViewById(R.id.password_edit);
        rePasswordEdit = (MaterialEditText) findViewById(R.id.re_password_edit);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        firstNameEdit = (MaterialEditText) findViewById(R.id.first_name_edit);
        lastNameEdit = (MaterialEditText) findViewById(R.id.last_name_edit);

        // Hide the sign up views until sign up is pressed
        rePasswordEdit.setVisibility(View.GONE);
        firstNameEdit.setVisibility(View.GONE);
        lastNameEdit.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);

        // Set listeners
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Load the signup intent
                //Intent makeAccountIntent = new Intent(signInContext, SignUpActivity.class);
                //startActivity(makeAccountIntent);

                new LoginTask().execute(usernameEdit.getText().toString(), passwordEdit.getText().toString());

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the sign up views until sign up is pressed
                rePasswordEdit.setVisibility(View.VISIBLE);
                firstNameEdit.setVisibility(View.VISIBLE);
                lastNameEdit.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.GONE);

                // Validate the fields, and report back
                boolean firstValid = firstNameEdit.validate("^(?=\\s*\\S).*$", getString(R.string.signup_name_error));
                boolean lastValid = lastNameEdit.validate("^(?=\\s*\\S).*$", getString(R.string.signup_name_error));
                boolean usernameValid = usernameEdit.validate("^(?=\\s*\\S).*$", getString(R.string.signup_username_error));
                boolean passwordValid = passwordEdit.getText().toString().equals(rePasswordEdit.getText().toString()) && !passwordEdit.getText().toString().equals("");
                if(!passwordValid){passwordEdit.setError(getString(R.string.signup_password_error));}

                if(firstValid && lastValid && usernameValid && passwordValid){
                    new AddUserAndLoginTask().execute(usernameEdit.getText().toString(),
                                                      passwordEdit.getText().toString(),
                                                      firstNameEdit.getText().toString(),
                                                      lastNameEdit.getText().toString());
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset and hide fields
                firstNameEdit.setText("");
                lastNameEdit.setText("");
                rePasswordEdit.setText("");
                cancelBtn.setVisibility(View.GONE);
                rePasswordEdit.setVisibility(View.GONE);
                firstNameEdit.setVisibility(View.GONE);
                lastNameEdit.setVisibility(View.GONE);
                signInBtn.setVisibility(View.VISIBLE);

            }
        });

        animatedCircleLoadingView.setAnimationListener(new AnimatedCircleLoadingView.AnimationListener() {
            @Override
            public void onAnimationEnd() {
                if(Globals.authObject.isValid()){
                    Intent intent = new Intent(signInContext, MainActivity.class);
                    startActivity(intent);
                    ((Activity) signInContext).finish();
                }
                else {
                    animatedCircleLoadingView.resetLoading();
                    animatedCircleLoadingView.setVisibility(View.GONE);
                    signUpBtn.setVisibility(View.VISIBLE);
                    firstNameEdit.setText("");
                    lastNameEdit.setText("");
                    rePasswordEdit.setText("");
                    cancelBtn.setVisibility(View.GONE);
                    rePasswordEdit.setVisibility(View.GONE);
                    firstNameEdit.setVisibility(View.GONE);
                    lastNameEdit.setVisibility(View.GONE);
                    signInBtn.setVisibility(View.VISIBLE);
                    if(Globals.authObject != null && Globals.authObject.getLastError() != null){
                        ViewHelper.showErrorDialog(signInContext, getString(R.string.error_title_login), Globals.authObject.getLastError());
                        Globals.authObject.setLastError(null);
                    }
                    else if(Globals.mainUser != null && Globals.mainUser.getLastError() != null){
                        ViewHelper.showErrorDialog(signInContext, getString(R.string.error_title_login), Globals.mainUser.getLastError());
                        Globals.mainUser.setLastError(null);
                    }

                }

            }
        });

    }

    /**
     * Downloads an access token from the server for making transactions
     */
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Globals.authObject.setUsername(params[0]);
            Globals.authObject.setPassword(params[1]);

            //Log.e("LOGIN", "Got username and password");

            Globals.authObject.authorize(true);

            //Log.e("LOGIN", "Attempting to authorize");

            if(Globals.authObject.isValid()){
                Globals.authObject.saveOAuthToPrefs(signInContext);
            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(!Globals.authObject.isValid()){
                animatedCircleLoadingView.stopFailure();
            }
            else {
                animatedCircleLoadingView.stopOk();
            }

        }

        @Override
        protected void onPreExecute() {

            animatedCircleLoadingView.setVisibility(View.VISIBLE);
            animatedCircleLoadingView.startIndeterminate();
            signUpBtn.setVisibility(View.GONE);
            signInBtn.setVisibility(View.GONE);

        }

    }

    /**
     * Adds a user account to the server
     */
    private class AddUserAndLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Globals.mainUser = new User();

            //Log.e("ADDUSER", "Got username and password");

            boolean added = Globals.mainUser.addUser(params[0], params[1], params[2], params[3]);

            //Log.e("LOGIN", "Attempt to add user: " + added);

            if(added){
                Globals.authObject.setUsername(params[0]);
                Globals.authObject.setPassword(params[1]);

                //Log.e("LOGIN", "Got username and password");

                Globals.authObject.authorize(true);

                //Log.e("LOGIN", "Attempting to authorize");

                Globals.authObject.saveOAuthToPrefs(signInContext);
            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(Globals.mainUser.isInDB() && Globals.authObject.isValid()){
                animatedCircleLoadingView.stopOk();
            }
            else {
                animatedCircleLoadingView.stopFailure();
            }

        }

        @Override
        protected void onPreExecute() {

            animatedCircleLoadingView.setVisibility(View.VISIBLE);
            animatedCircleLoadingView.startIndeterminate();
            signUpBtn.setVisibility(View.GONE);
            signInBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);

        }

    }

}
