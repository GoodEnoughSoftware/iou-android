package com.wrongwaystudios.iou;

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

public class CreateIOU extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private UserAdapter senderAdapter;
    private UserAdapter receiverAdapter;
    private MaterialAutoCompleteTextView senderAutoComplete;
    private MaterialAutoCompleteTextView receiverAutoComplete;
    private Context context = this;
    private UserSearchObject[] senderUsers;
    private UserSearchObject[] receiverUsers;

    private String BASE_FIND_URL = "api/users/find?";
    private final int QUERY_LIMIT = 10;

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
                Log.e("Create IOU: ", "" + valid);
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

}
