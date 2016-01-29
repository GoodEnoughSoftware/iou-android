package com.wrongwaystudios.iou;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wrongwaystudios.iou.resources.Constructors;
import com.wrongwaystudios.iou.resources.Globals;
import com.wrongwaystudios.iou.resources.Transaction;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    private Transaction lookingIOU = null;
    private boolean isEditing = false;
    private FloatingActionButton fabEdit = null;
    private FloatingActionButton fabForgive = null;
    private FloatingActionButton fabPay = null;

    // All view
    private TextView senderText = null;
    private TextView receiverText = null;

    // Constants
    private final String BASE_FORGIVE_URL = "api/ious/forgive/";
    private final Context detailContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lookingIOU = getIntent().getParcelableExtra("iouObject");
        isEditing = getIntent().getExtras().getBoolean("isEditing");

        this.setTitle(String.format(getResources().getString(R.string.detail_title), lookingIOU.getSenderUsername()));

        fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fabForgive = (FloatingActionButton) findViewById(R.id.fab_forgive);
        fabPay = (FloatingActionButton) findViewById(R.id.fab_pay);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditing){
                    saveIOU();
                }
                else{
                    editIOU();
                }
                isEditing = !isEditing;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set scroll listeners
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.detail_scroll);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY - oldScrollY;
                if (dy > 0 && fabEdit.isShown()) {
                    if (fabEdit.isShown()) {
                        fabEdit.hide();
                    }
                    if (fabForgive.isShown()) {
                        fabForgive.hide();
                    }
                    if (fabPay.isShown()) {
                        fabPay.hide();
                    }
                }
                else if (dy < 0) {
                    if (!fabEdit.isShown()) {
                        fabEdit.show();
                    }
                    if (!fabForgive.isShown()) {
                        fabForgive.show();
                    }
                    if (!fabPay.isShown()) {
                        fabPay.show();
                    }
                }
            }
        });

        fabForgive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickForgive();
            }
        });

    }

    protected void onStart(){

        super.onStart();

        if(lookingIOU == null){
            lookingIOU = getIntent().getParcelableExtra("iouObject");
        }

        // Grab each view and load information
        senderText = (TextView) findViewById(R.id.sender_textview);
        receiverText = (TextView) findViewById(R.id.receiver_textview);
        TextView createdOnText = (TextView) findViewById(R.id.created_on_label);
        TextView statusDetail = (TextView) findViewById(R.id.status_detail);
        TextView amountDetail = (TextView) findViewById(R.id.amount_detail);

        senderText.setText(lookingIOU.getSenderUsername());
        receiverText.setText(lookingIOU.getRecipientUsername());

        String strDateFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String creationText = sdf.format(lookingIOU.getCreated());
        createdOnText.setText(String.format(getResources().getString(R.string.iou_details_created_on), creationText));
        statusDetail.setText(Globals.statusString(lookingIOU.getIouStatus()));

        DecimalFormat df = new DecimalFormat("#.00");
        amountDetail.setText(String.format(getResources().getString(R.string.iou_detail_amount), df.format(lookingIOU.getAmount())));

        if(isEditing){
            editIOU();
        }
        else {
            displayInformation();
        }

    }

    private void saveIOU(){

        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_white));

    }

    private void editIOU(){

        fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_white));

    }

    /**
     * Action when the forgive fab is clicked (shows a dialog to confirm)
     */
    private void clickForgive(){

        if(lookingIOU.getSenderUsername().equals(Globals.mainUser.getUsername())){
            new MaterialDialog.Builder(this)
                    .title(R.string.forgive_bad_title)
                    .content(R.string.forgive_bad_content)
                    .positiveText(R.string.forgive_bad_ok)
                    .build().show();
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.forgive_title)
                    .content(R.string.forgive_content)
                    .positiveText(R.string.forgive_accept)
                    .negativeText(R.string.forgive_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new ForgiveIOUTask().execute(lookingIOU);
                        }
                    })
                    .build().show();
        }

    }

    private void displayInformation(){

    }

    private class ForgiveIOUTask extends AsyncTask<Transaction, Void, Void> {

        boolean success = false;

        @Override
        protected Void doInBackground(Transaction ... params) {

            String URL = Globals.BASE_API_URL + BASE_FORGIVE_URL;

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("id", params[0].getId());

            try{

                JSONObject result = Constructors.putData(URL, parameters, Globals.authObject.getAccessToken());

                if(result.has("success")){
                    success = result.getBoolean("success");
                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(success){
                ((Activity) detailContext).finish();
            }
        }
    }

}
