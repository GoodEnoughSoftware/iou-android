package com.wrongwaystudios.iou.resources;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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

            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            conn.setRequestProperty("charset","utf-8");

            if(authorization != null){
                conn.setRequestProperty("Authorization", authorization);
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

            // Return the result
            return new JSONObject(response.toString());
        }
        catch (Exception e) {
            //Log.e("******", "" + e.getMessage());
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

}
