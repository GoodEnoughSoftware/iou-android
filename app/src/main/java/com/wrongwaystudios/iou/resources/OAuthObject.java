package com.wrongwaystudios.iou.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Handles, saves, and creates information used for
 * OAuth 2.0 access on the server
 * @author Aaron Vontell
 * @date 1/20/16
 * @version 0.1
 */
public class OAuthObject {

    private Context appContext = null;

    private String accessToken = null;
    private long expirationTime = -1;
    private boolean valid = false;
    private String username = null;
    private String password = null;

    // Constants used in URL building and field building / retrieval
    private final String BASE_OAUTH_URL = "oauth/token/";
    private final String grantTypeField = "grant_type";         private final String grantType = "password";
    private final String clientIdField = "client_id";
    private final String clientSecretField = "client_secret";
    private final String usernameField = "username";
    private final String passwordField = "password";
    private final String accessTokenField = "access_token";
    private final String expirationField = "expires_in";

    private String lastError = null;

    /**
     * Creates an OAuth Object with the given username
     * and password, ready to start authorization
     * @param context The context of the calling application
     */
    public OAuthObject(Context context){

        this.appContext = context;

    }

    /**
     * Loads an OAuthObject, first by checking saved preferences,
     * and otherwise by checking. Then saves the result. Run this
     * off of the UI thread
     */
    public void authorize(boolean noLocalLoad){

        // First, attempt to load OAuth from the prefs
        if(!noLocalLoad){
            loadOAuthFromPrefs(this.appContext);
        }

        Log.e("AUTHOR", "Auth is valid: " + valid);

        // If not yet valid, download the access token
        if(!valid) {
            runOAuthFromServer(username, password);
        }

        if(valid){
            saveOAuthToPrefs(this.appContext);
        }

    }

    /**
     * Saves the OAuth object to shared preference for later use
     * Saved in the form of "accesstoken:expirationunix:username:password"
     * @param context The context of the calling application
     */
    public void saveOAuthToPrefs(Context context){

        SharedPreferences preferences = context.getSharedPreferences(Globals.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String data = "";
        data += accessToken + ":";
        data += expirationTime + ":";
        data += username + ":";
        data += password;

        Log.e("SAVE", "Saving auth: " + data);

        editor.putString(Globals.OAUTH_KEY, data);

        editor.apply();

    }

    /**
     * Deletes all stored information about this oauth token
     * @param context The calling activity
     */
    public void delete(Context context){

        SharedPreferences preferences = context.getSharedPreferences(Globals.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(Globals.OAUTH_KEY);
        editor.apply();

    }

    /**
     * Loads the latest OAuth object from preferences
     * @param context The context of the calling application
     * @return The OAuth object, or null if it does not exist
     */
    private void loadOAuthFromPrefs(Context context){

        SharedPreferences preferences = context.getSharedPreferences(Globals.PREFS_KEY, Context.MODE_PRIVATE);
        String existingAuth = preferences.getString(Globals.OAUTH_KEY, "");

        Log.e("LOAD", "Loading existing auth: " + existingAuth);

        if(existingAuth.equals("")){
            valid = false;
        }
        else{
            String[] items = existingAuth.split(":");
            if(items.length != 4){
                valid = false;
            } else{
                String token = items[0];
                long expire = Long.parseLong(items[1]);
                String user = items[2];
                String pass = items[3];

                if(expire <= System.currentTimeMillis() / 1000){
                    valid = false;
                    username = user;
                    password = pass;
                }
                else {
                    accessToken = token;
                    expirationTime = expire;
                    username = user;
                    password = pass;
                    valid = true;
                }

            }

        }

    }

    /**
     * Grabs the access token from the server, given username and password
     * @param usernameSend The username of the current user
     * @param passwordSend The password of the current user
     */
    private void runOAuthFromServer(String usernameSend, String passwordSend){

        // Set the parameters
        HashMap<String, String> params = new HashMap<>();
        params.put(grantTypeField, grantType);
        params.put(clientIdField, Globals.CLIENT_ID);
        params.put(clientSecretField, Globals.CLIENT_SECRET);
        params.put(usernameField, usernameSend);
        params.put(passwordField, passwordSend);

        // POST the data and obtain a JSON result
        JSONObject result = Constructors.postData(Globals.BASE_API_URL + BASE_OAUTH_URL, params, null);

        // Parse the result
        try {

            Log.e("AUTHRES:", result.toString());

            if(!result.has("error")){
                accessToken = result.getString(accessTokenField);
                expirationTime = result.getInt(expirationField) + (System.currentTimeMillis() / 1000);
                valid = true;
            }
            else {
                lastError = result.getString("error");
                valid = false;
            }

        } catch (Exception e){

            Log.e("AUTH ERROR", e.getMessage());
            lastError = e.getMessage();
            accessToken = null;
            expirationTime = -1;

        }

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isValid() {
        return valid;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}