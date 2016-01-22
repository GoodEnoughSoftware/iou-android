package com.wrongwaystudios.iou.resources;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A class that represents a user from the database
 * @author Aaron Vontell
 * @date 1/21/16
 * @version 0.1
 */
public class User {

    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Bitmap profilePic;

    private final String BASE_USER_URL = "api/users";
    private final String BASE_NOTI_URL = "api/notifications/";

    private final String clientIdField = "client_id";
    private final String clientSecretField = "client_secret";
    private final String usernameField = "username";
    private final String passwordField = "password";
    private final String successField = "success";

    public UserNotification[] notifications;

    private boolean inDB = false;
    private String lastError = null;

    public User(String username){
        this.username = username;
    }

    public User(){}

    /**
     * Adds a user to the database
     * @param username The desired username
     * @param password The desired password
     * @return whether or not the user was successfully added
     */
    public boolean addUser(String username, String password){

        this.username = username;

        // Set the parameters
        HashMap<String, String> params = new HashMap<>();
        params.put(clientIdField, Globals.CLIENT_ID);
        params.put(clientSecretField, Globals.CLIENT_SECRET);
        params.put(usernameField, username);
        params.put(passwordField, password);

        // POST the data and obtain a JSON result
        JSONObject result = Constructors.postData(Globals.BASE_API_URL + BASE_USER_URL, params, null);

        // Parse the result
        try {

            //Log.e("ADDUSER", result.toString());

            inDB = result.getBoolean(successField);

            if(result.has("error")){
                lastError = result.getString("error");
            }

            return result.getBoolean(successField);

        } catch (Exception e){

            //Log.e("USER ERROR", e.getMessage());

            lastError = e.getMessage();

            return false;

        }

    }

    public boolean getNotifications(){

        JSONArray result = Constructors.getDataAsArray(Globals.BASE_API_URL + BASE_NOTI_URL, Globals.authObject.getAccessToken());

        try {

            notifications = new UserNotification[result.length()];
            for(int i = 0; i < notifications.length; i++){
                JSONObject notif = result.getJSONObject(i);
                String id = notif.getString("id");
                String message = notif.getString("message");
                notifications[i] = new UserNotification(id, message);
            }

            return true;


        } catch (Exception e) {

            return false;

        }

    }

    public boolean isInDB() {
        return inDB;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }



}
