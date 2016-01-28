package com.wrongwaystudios.iou.resources;

import android.content.Context;

/**
 * A class that holds global variables
 * @author Aaron Vontell
 * @date 1/20/16
 * @version 0.1
 */
public class Globals {

    public static final String PREFS_KEY = "IOU_PREFERENCES_KEY";
    public static final String OAUTH_KEY = "IOU_OAUTH_KEY";

    public static final String CLIENT_ID = "app";
    public static final String CLIENT_SECRET = "d0gd4ys";

    public static final String BASE_API_URL = "http://18.111.29.250/";

    public static OAuthObject authObject = null;
    public static User mainUser = null;

    public static Context globalContext = null;

    public static String statusString(IOUStatus status) {

        switch (status){
            case ACTIVE:
                return "Active";
            case PENDING:
                return "Pending";
            case COMPLETED:
                return "Completed";
            case FORGIVEN:
                return "Forgiven";
            default:
                return "Pending";
        }

    }

}
