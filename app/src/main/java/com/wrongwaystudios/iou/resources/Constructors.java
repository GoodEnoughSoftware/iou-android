package com.wrongwaystudios.iou.resources;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that provides static variables for conducting
 * HTTP calls and creating its required components
 * @author Aaron Vontell
 * @date 1/20/15
 * @version 0.2
 */
public class Constructors {

    /**
     * Creates and executes a HTTP POST request using the
     * application/x-www-form-urlencoded content type
     * @param url The url to the API endpoint you are hitting
     * @param data The data to send as a mapping of key - value pairs
     * @return The response from the server as a JSON Object
     */
    public static JSONObject postData(final String url, final HashMap<String, String> data, String authorization){

        HttpURLConnection conn = null;

        try {

            // Construct the POST
            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            String postData = "";
            for(String key : data.keySet()){
                postData += key + "=" + data.get(key) + "&";
            }
            postData = postData.substring(0, postData.length() - 1);

            Log.e("POST", postData);

            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            conn.setRequestProperty("charset","utf-8");

            if(authorization != null){
                conn.setRequestProperty("Authorization", "Bearer " + authorization);
            }

            // Begin writing to the server
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            Log.e("WRITE", "");

            //Log.e("SERVER:" , "" + conn.getResponseCode());

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.e("READ", "");

            // Return the result
            return new JSONObject(response.toString());
        }
        catch (Exception e) {
            Log.e("******", "" + e);
            return new JSONObject();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    /**
     * Creates and executes a HTTP POST request using the
     * application/json content type
     * @param url The url to the API endpoint you are hitting
     * @param data The data to send as a JSON Object
     * @return The response from the server as a JSON Object
     */
    public static JSONObject postData(final String url, final JSONObject data, String authorization){

        HttpURLConnection conn = null;

        try {

            // Construct the POST
            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setRequestMethod("POST");

            String postData = data.toString();

            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));

            if(authorization != null){
                conn.setRequestProperty("Authorization", "Bearer " + authorization);
            }

            // Begin writing to the server
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            Log.e("SERVER:" , "" + conn.getResponseCode());

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Return the result
            return new JSONObject(response.toString());
        }
        catch (Exception e) {
            Log.e("******", e.getMessage());
            return new JSONObject();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    /**
     * Creates and executes an HTTP GET request using
     * the HttpURLConnection class.
     * @param url The url to the API endpoint you are hitting
     * @param authorization The response from the server as a JSON Object
     * @return
     */
    public static JSONObject getData(final String url, String authorization){

        HttpURLConnection conn = null;

        // Construct the GET
        try {

            // Construct the GET
            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setRequestMethod("GET");

            if(authorization != null){
                conn.setRequestProperty("Authorization", "Bearer " + authorization);
            }

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Return the result
            return new JSONObject(response.toString());
        }
        catch (Exception e) {
            //Log.e("******", e.getMessage());
            return new JSONObject();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    /**
     * Creates and executes an HTTP GET request using
     * the HttpURLConnection class.
     * @param url The url to the API endpoint you are hitting
     * @param authorization The response from the server as a JSON Object
     * @return
     */
    public static JSONArray getDataAsArray(final String url, String authorization){

        HttpURLConnection conn = null;

        // Construct the GET
        try {

            // Construct the GET
            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");

            if(authorization != null){
                conn.setRequestProperty("Authorization", "Bearer " + authorization);
            }

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                //Log.e("SERVED", inputLine);
                response.append(inputLine);
            }
            in.close();

            //Log.e("CONSTR:", response.toString());

            // Return the result
            return new JSONArray(response.toString());
        }
        catch (Exception e) {

            Log.e("ERROR", e.toString());

            return new JSONArray();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    public static JSONObject deleteData(final String url, final HashMap<String, String> data, String authorization){

        HttpURLConnection conn = null;

        try {

            // Construct the POST
            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("DELETE");

            String postData = "";
            for(String key : data.keySet()){
                postData += key + "=" + data.get(key) + "&";
            }
            postData = postData.substring(0, postData.length() - 1);

            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            conn.setRequestProperty("charset","utf-8");

            if(authorization != null){
                conn.setRequestProperty("Authorization", "Bearer " + authorization);
            }

            // Begin writing to the server
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            //Log.e("SERVER:" , "" + conn.getResponseCode());

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.e("DELETE", response.toString());

            // Return the result
            return new JSONObject(response.toString());
        }
        catch (Exception e) {
            Log.e("******", "" + e.getMessage());
            return new JSONObject();
        } finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    public static JSONObject postDataOkHTTP(final String url, final HashMap<String, String> data, String authorization) throws IOException, JSONException{

        OkHttpClient client = new OkHttpClient();

        String postData = "";
        for(String key : data.keySet()){
            postData += key + "=" + data.get(key) + "&";
        }
        postData = postData.substring(0, postData.length() - 1);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, postData);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("authorization", "Bearer " + authorization)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "cc0eb45b-993d-b7d4-1f40-201302c38a9d")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response = client.newCall(request).execute();

        return new JSONObject(response.body().string());

    }

}
