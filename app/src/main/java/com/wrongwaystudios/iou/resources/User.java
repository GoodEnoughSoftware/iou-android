package com.wrongwaystudios.iou.resources;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private double netWorth;

    private final String BASE_USER_URL = "api/users";
    private final String BASE_NOTI_URL = "api/notifications/";
    private final String BASE_IOUS_ACT_URL = "api/ious/active/";
    private final String BASE_IOUS_PEN_URL = "api/ious/pending/";
    private final String BASE_PEN_ACC_URL = "api/ious/accept/";
    private final String BASE_PEN_REJ_URL = "api/ious/reject/";
    private final String BASE_NET_WORTH_URL = "api/users/total/";

    private final String clientIdField = "client_id";
    private final String clientSecretField = "client_secret";
    private final String usernameField = "username";
    private final String passwordField = "password";
    private final String firstNameField = "firstName";
    private final String lastNameField = "lastName";
    private final String successField = "success";
    private final String totalNetField = "total";

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
    public boolean addUser(String username, String password, String firstName, String lastName){

        this.username = username;
        this.fullName = firstName + " " + lastName;

        // Set the parameters
        HashMap<String, String> params = new HashMap<>();
        params.put(clientIdField, Globals.CLIENT_ID);
        params.put(clientSecretField, Globals.CLIENT_SECRET);
        params.put(usernameField, username);
        params.put(passwordField, password);
        params.put(firstNameField, firstName);
        params.put(lastNameField, lastName);

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
     * @return Whether or not this operation was a success
     */
    public boolean getActiveTransactions(){

        return getIOUs(Globals.BASE_API_URL + BASE_IOUS_ACT_URL, IOUStatus.ACTIVE);

    }

    /**
     * Gets a list of pending IOUs from the server
     * @return Whether or not this operation was a success
     */
    public boolean getPendingTransactions(){

        return getIOUs(Globals.BASE_API_URL + BASE_IOUS_PEN_URL, IOUStatus.PENDING);

    }

    /**
     * Accepts the pending iou
     * @param iouId The IOU to accept
     * @return whether or not this operation worked
     */
    public void acceptPendingIOU(String iouId){

        new AcceptIouTask().execute(iouId);

    }

    /**
     * Rejects the pending IOU
     * @param iouId The IOU to reject
     * @return whether or not this operation worked
     */
    public void rejectPendingIOU(String iouId){

        new RejectIouTask().execute(iouId);

    }

    /**
     * Downloads IOUs from the given url
     * @param URL
     * @param type
     * @return Whether or not this operation was a success
     */
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

    /**
     * Retreives the net worth of the current user
     * @return whether or not this operation was successful
     */
    public boolean getNetWorthTask(){

        JSONObject result = Constructors.getData(Globals.BASE_API_URL + BASE_NET_WORTH_URL, Globals.authObject.getAccessToken());

        try {
            if(result.has(totalNetField)){
                netWorth = result.getDouble(totalNetField);
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
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

    public double getNetWorth() { return netWorth; }

    /**
     * Accepts an iou from the server
     */
    private class AcceptIouTask extends AsyncTask<String, Void, String> {

        boolean iouAccept = false;

        @Override
        protected String doInBackground(String ... params) {

            String iouId = params[0];

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("id", iouId);

            try {
                JSONObject result = Constructors.putData(Globals.BASE_API_URL + BASE_PEN_ACC_URL, parameters, Globals.authObject.getAccessToken());
                if(result.has("success")){
                    iouAccept = result.getBoolean("success");
                }
                iouAccept = false;
            } catch (Exception e) {
                iouAccept = false;
            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(iouAccept){
                Log.e("ACCEPT", "IOU was accepted");
            } else {
                Log.e("ACCEPT", "IOU was not accepted");
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Rejects an iou from the server
     */
    private class RejectIouTask extends AsyncTask<String, Void, String> {

        boolean iouReject = false;

        @Override
        protected String doInBackground(String ... params) {

            String iouId = params[0];

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("id", iouId);

            try {
                JSONObject result = Constructors.putData(Globals.BASE_API_URL + BASE_PEN_REJ_URL, parameters, Globals.authObject.getAccessToken());
                if(result.has("success")){
                    iouReject = result.getBoolean("success");
                }
                iouReject = false;
            } catch (Exception e) {
                iouReject = false;
            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(iouReject){
                Log.e("REJECT", "IOU was rejected");
            } else {
                Log.e("REJECT", "IOU was not rejected");
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

}
