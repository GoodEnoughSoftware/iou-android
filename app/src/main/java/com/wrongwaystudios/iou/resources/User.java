package com.wrongwaystudios.iou.resources;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    private final String BASE_IOUS_ACT_URL = "api/ious/active/";

    private final String clientIdField = "client_id";
    private final String clientSecretField = "client_secret";
    private final String usernameField = "username";
    private final String passwordField = "password";
    private final String successField = "success";

    // IOU Fields
    private final String idField = "_id";
    private final String payerField = "payer";
    private final String amountField = "amount";
    private final String recipientField = "recipient";
    private final String descriptionField = "description";
    private final String createdField = "created";
    private final String createdByField = "createdBy";
    private final String lastRemindedField = "lastReminded";
    private final String dueDateField = "dueDate";

    public ArrayList<UserNotification> notifications;
    public ArrayList<Transaction> allIOUs;

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

    /**
     * Gets a list of active IOUs from the server
     * @return
     */
    public boolean getActiveTransactions(){

        return getIOUs(Globals.BASE_API_URL + BASE_IOUS_ACT_URL, IOUStatus.ACTIVE);

    }

    private boolean getIOUs(String URL, IOUStatus type){

        JSONArray result = Constructors.getDataAsArray(URL, Globals.authObject.getAccessToken());

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            allIOUs = new ArrayList<>();
            for(int i = 0; i < result.length(); i++){
                JSONObject iou = result.getJSONObject(i);

                String id = null;
                String payer = null;
                double amount = 0;
                String recipient = null;
                String description = null;
                Date created = null;
                String createdBy = null;
                Date lastReminded = null;
                Date dueDate = null;

                if(iou.has(idField)){id = iou.getString(idField);}
                if(iou.has(payerField)){payer = iou.getString(payerField);}
                if(iou.has(amountField)){amount = iou.getDouble(amountField);}
                if(iou.has(recipientField)){recipient = iou.getString(recipientField);}
                if(iou.has(descriptionField) && iou.getString(descriptionField) != null){description = iou.getString(descriptionField);}
                if(iou.has(createdField)){created = dateFormat.parse(iou.getString(createdField).substring(0,9));}
                if(iou.has(createdByField)){createdBy = iou.getString(createdByField);}
                if(iou.has(lastRemindedField)){lastReminded = dateFormat.parse(iou.getString(lastRemindedField).substring(0,9));}
                if(iou.has(dueDateField) && iou.getString(dueDateField).length() != 4){dueDate = dateFormat.parse(iou.getString(dueDateField).substring(0,9));}

                Transaction newIou = new Transaction(id, amount, recipient, payer, dueDate, type, description, created, createdBy, lastReminded);

                allIOUs.add(newIou);

            }

            return true;

        } catch (Exception e) {

            Log.e("***", e.toString());

            return false;

        }

    }

    /**
     * Gets a list of notifications from the server
     * @return whether or not the notification get was successful
     */
    public boolean getNotifications(){

        JSONArray result = Constructors.getDataAsArray(Globals.BASE_API_URL + BASE_NOTI_URL, Globals.authObject.getAccessToken());

        notifications = new ArrayList<>();

        try {

            notifications = new ArrayList<>();
            for(int i = 0; i < result.length(); i++){
                JSONObject notif = result.getJSONObject(i);
                String id = notif.getString("id");
                String message = notif.getString("message");
                notifications.add(new UserNotification(id, message));
            }

            return true;


        } catch (Exception e) {

            return false;

        }

    }

    /**
     * Delete notification at the given position
     * @param pos Notification number to remove
     * @return whether or not this was removed
     */
    public boolean deleteNotification(int pos){

        HashMap<String, String> params = new HashMap<>();
        params.put("id", Globals.mainUser.notifications.get(pos).getId());

        JSONObject result = Constructors.deleteData(Globals.BASE_API_URL + BASE_NOTI_URL, params, Globals.authObject.getAccessToken());

        try {
            boolean success = result.getBoolean("success");
            Log.e("Delete Notif: ", "" + success);
            return success;
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

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }
}
