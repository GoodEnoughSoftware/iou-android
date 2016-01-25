package com.wrongwaystudios.iou.resources;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wrongwaystudios.iou.R;

/**
 * A class to help manipulate views and show dialogs
 * @author Aaron Vontell
 * @date 1/20/16
 * @version 0.1
 */
public class ViewHelper {

    public static MaterialDialog loadingDialog = null;

    /**
     * Shows a loading dialog with an indeterminate loader
     * @param title The title of the dialog
     * @param content The content of the dialog
     * @param cancellable Whether or not this dialog should be dismissable
     */
    public static void showLoadingDialog(String title, String content, boolean cancellable){

    }

    /**
     * Shows an error dialog after an error occurs
     * @param context The calling activity
     * @param title The title of the dialog
     * @param content The content or error to show
     */
    public static void showErrorDialog(Context context, String title, String content){

        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(R.string.error_ok)
                .build().show();

    }

    /**
     * Starts the check operation on an IOU
     * @param pos
     */
    public static void startIouCheckOperation(int pos){

        Transaction iou = Globals.mainUser.allIOUs.get(pos);

    }

    /**
     * Starts the edit operation on an IOU
     * @param pos
     */
    public static void startIouEditOperation(int pos){

        Transaction iou = Globals.mainUser.allIOUs.get(pos);

    }

    /**
     * Starts the notify operation on an IOU
     * @param pos
     */
    public static void startIouNotifyOperation(int pos){

        Transaction iou = Globals.mainUser.allIOUs.get(pos);

    }



}
