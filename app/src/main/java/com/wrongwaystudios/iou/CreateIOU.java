package com.wrongwaystudios.iou;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wrongwaystudios.iou.resources.Constructors;
import com.wrongwaystudios.iou.resources.Globals;
import com.wrongwaystudios.iou.resources.UserAdapter;
import com.wrongwaystudios.iou.resources.UserSearchObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class CreateIOU extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private UserAdapter senderAdapter;
    private UserAdapter receiverAdapter;
    private MaterialAutoCompleteTextView senderAutoComplete;
    private MaterialAutoCompleteTextView receiverAutoComplete;
    private Context context = this;
    private UserSearchObject[] senderUsers;
    private UserSearchObject[] receiverUsers;

    private final String BASE_FIND_URL = "api/users/find?";
    private final String BASE_CREATE_IOU_SELF_URL = "api/ious/self/";
    private final String BASE_CREATE_IOU_OTHER_URL = "api/ious/other/";
    private final int QUERY_LIMIT = 10;

    private final String USERNAME_FIELD = "username";
    private final String AMOUNT_FIELD = "amount";
    private final String DESCRIPTION_FIELD = "description";
    private final String DUE_DATE_FIELD = "dueDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_iou);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = validate();
                if(valid){
                    new CreateTask().execute();
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views configurations -------------------------------
        senderAutoComplete = (MaterialAutoCompleteTextView) findViewById(R.id.sender_edit);
        senderAdapter = new UserAdapter(this, R.layout.user_dropdown, new UserSearchObject[0]);
        senderAutoComplete.setAdapter(senderAdapter);

        senderAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                new GetSenderUsersTask().execute(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        receiverAutoComplete = (MaterialAutoCompleteTextView) findViewById(R.id.recipient_edit);
        receiverAdapter = new UserAdapter(this, R.layout.user_dropdown, new UserSearchObject[0]);
        receiverAutoComplete.setAdapter(receiverAdapter);

        receiverAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                new GetReceiverUsersTask().execute(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Configure the date picker
        findViewById(R.id.date_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        findViewById(R.id.date_edit).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePicker();
                }
            }
        });

    }

    /**
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        ((MaterialEditText) findViewById(R.id.date_edit)).setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + year);


    }

    public void showDatePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                CreateIOU.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    /**
     * Validates all views on the page
     * @return whether or not this was a valid transaction
     */
    public boolean validate(){

        MaterialAutoCompleteTextView senderField = (MaterialAutoCompleteTextView) findViewById(R.id.sender_edit);
        boolean senderValid = senderField.validate("^(?=\\s*\\S).*$", getString(R.string.iou_create_sender_error));

        MaterialAutoCompleteTextView receiverField = (MaterialAutoCompleteTextView) findViewById(R.id.recipient_edit);
        boolean receiverValid = receiverField.validate("^(?=\\s*\\S).*$", getString(R.string.iou_create_receiver_error));

        MaterialEditText amountField = (MaterialEditText) findViewById(R.id.money_edit);
        boolean amountValid = amountField.validate("\\d+(\\.\\d+)*", getString(R.string.iou_create_amount_error));

        return senderValid && receiverValid && amountValid;

    }

    /**
     * Makes the post request for the iou
     * @return whether or not the request was made
     */
    public boolean sendCreate(){

        MaterialAutoCompleteTextView senderField = (MaterialAutoCompleteTextView) findViewById(R.id.sender_edit);
        MaterialAutoCompleteTextView receiverField = (MaterialAutoCompleteTextView) findViewById(R.id.recipient_edit);
        MaterialEditText amountField = (MaterialEditText) findViewById(R.id.money_edit);
        MaterialEditText dateField = (MaterialEditText) findViewById(R.id.date_edit);
        MaterialEditText noteField = (MaterialEditText) findViewById(R.id.note_edit);

        String sender = senderField.getText().toString();
        String receiver = receiverField.getText().toString();
        String amount = amountField.getText().toString();
        String date = dateField.getText().toString();
        String note = noteField.getText().toString();

        String url = "";

        HashMap<String, String> params = new HashMap<>();

        // Send self IOU
        if(sender.equals(receiver)){
            // Send error, can't have the same user
        }
        else if(sender.equals(Globals.mainUser.getUsername())) {
            params.put(USERNAME_FIELD, receiver);
            url = Globals.BASE_API_URL + BASE_CREATE_IOU_SELF_URL;
        }
        else if (receiver.equals(Globals.mainUser.getUsername())) {
            params.put(USERNAME_FIELD, sender);
            url = Globals.BASE_API_URL + BASE_CREATE_IOU_OTHER_URL;
        }
        else {
            // Send error, need to be a receiver or sender
        }

        params.put(AMOUNT_FIELD, amount);
        if(!note.equals("")){params.put(DESCRIPTION_FIELD, note);}
        if(!date.equals("")){params.put(DUE_DATE_FIELD, date);}

        try {

            JSONObject result = Constructors.postDataOkHTTP(url, params, Globals.authObject.getAccessToken());

            return result.getBoolean("success");

        } catch (Exception e) {

            Log.e("ERROR", e.getMessage());

            return false;

        }

    }

    /**
     * Query's the server for users
     */
    private class GetSenderUsersTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String query = params[0];
            String url = Globals.BASE_API_URL + BASE_FIND_URL + "query=" + query + "&limit=" + QUERY_LIMIT;
            JSONArray result = Constructors.getDataAsArray(url, Globals.authObject.getAccessToken());

            try {

                senderUsers = new UserSearchObject[result.length()];

                for(int i = 0; i < result.length(); i++){

                    JSONObject jObj = result.getJSONObject(i);
                    String name = jObj.getString("firstName") + " " + jObj.getString("lastName");
                    String username = jObj.getString("username");

                    senderUsers[i] = new UserSearchObject(username, name);

                }

            } catch (Exception e) {

                Log.e("ERROR", e.toString());

            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            senderAdapter = new UserAdapter(context, R.layout.user_dropdown, senderUsers);
            senderAutoComplete.setAdapter(senderAdapter);
            senderAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Query's the server for users
     */
    private class GetReceiverUsersTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String query = params[0];
            String url = Globals.BASE_API_URL + BASE_FIND_URL + "query=" + query + "&limit=" + QUERY_LIMIT;
            JSONArray result = Constructors.getDataAsArray(url, Globals.authObject.getAccessToken());

            try {

                receiverUsers = new UserSearchObject[result.length()];

                for(int i = 0; i < result.length(); i++){

                    JSONObject jObj = result.getJSONObject(i);
                    String name = jObj.getString("firstName") + " " + jObj.getString("lastName");
                    String username = jObj.getString("username");

                    receiverUsers[i] = new UserSearchObject(username, name);

                }

            } catch (Exception e) {

                Log.e("ERROR", e.toString());

            }

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            receiverAdapter = new UserAdapter(context, R.layout.user_dropdown, receiverUsers);
            receiverAutoComplete.setAdapter(receiverAdapter);
            receiverAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Query's the server for users
     */
    private class CreateTask extends AsyncTask<String, Void, String> {

        boolean success = false;

        @Override
        protected String doInBackground(String... params) {

            success = sendCreate();

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(success){
                ((Activity) context).finish();
            }
            else {
                new MaterialDialog.Builder(context)
                        .title(R.string.iou_create_error_title)
                        .positiveText(R.string.iou_create_error_ok)
                        .content(R.string.iou_create_error_content)
                        .build().show();

            }

        }

        @Override
        protected void onPreExecute() {



        }

    }



}
