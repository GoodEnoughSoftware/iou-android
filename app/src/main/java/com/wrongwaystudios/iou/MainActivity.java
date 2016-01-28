package com.wrongwaystudios.iou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wrongwaystudios.iou.resources.Globals;
import com.wrongwaystudios.iou.resources.NotificationAdapter;
import com.wrongwaystudios.iou.resources.OAuthObject;
import com.wrongwaystudios.iou.resources.Transaction;
import com.wrongwaystudios.iou.resources.TransactionsAdapter;
import com.wrongwaystudios.iou.resources.User;
import com.wrongwaystudios.iou.resources.UserNotification;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context thisActivity = this;
    private DrawerLayout drawer = null;

    private RecyclerView navigationViewRight;
    private NotificationAdapter notificationAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SwipeRefreshLayout iouRefreshLayout;
    private TransactionsAdapter transactionAdapter;
    private RecyclerView.LayoutManager iouLayoutManager;
    private RecyclerView iouRecycler;

    private TextView usernameLabel = null;
    private TextView fullNameLabel = null;

    private int currentTask = R.id.nav_ious;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startCreateActivity();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Globals.globalContext = this;

        // Navigation view code --------------------------------------------------------------------

        NavigationView navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        View header = navigationViewLeft.getHeaderView(0);
        usernameLabel = (TextView) header.findViewById(R.id.username_label);
        fullNameLabel = (TextView) header.findViewById(R.id.fullname_label);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        navigationViewRight = (RecyclerView) findViewById(R.id.recycler_view_right);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        navigationViewRight.setLayoutManager(layoutManager);
        // Initially null
        ArrayList<UserNotification> emptyNotifs = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(emptyNotifs);
        navigationViewRight.setAdapter(notificationAdapter);
        notificationAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int del = viewHolder.getAdapterPosition();
                new DeleteNotificationsTask().execute(del);
                //navigationViewRight.removeViewAt(del);
                Globals.mainUser.notifications.remove(del);
                notificationAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                //notificationAdapter.notifyDataSetChanged();
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetNotificationsTask().execute();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(navigationViewRight);

        // Main IOU code ---------------------------------------------------------------------------

        iouRecycler = (RecyclerView) findViewById(R.id.iou_recycler_view);
        iouRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.iouRefreshLayout);
        iouLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        iouRecycler.setLayoutManager(iouLayoutManager);
        // Initially null
        ArrayList<Transaction> emptyIous = new ArrayList<>();
        transactionAdapter = new TransactionsAdapter(emptyIous, R.layout.active_iou_view);
        iouRecycler.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();

        iouRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                int id = currentTask;

                if (id == R.id.nav_ious) {
                    new GetActiveIousTask().execute();
                } else if (id == R.id.nav_pending) {
                    new GetPendingIousTask().execute();
                } else if (id == R.id.nav_previous) {

                } else if (id == R.id.nav_friends) {

                } else if (id == R.id.nav_create) {
                    startCreateActivity();
                } else if (id == R.id.nav_edit_prof) {

                } else if (id == R.id.nav_sign_out) {
                    signOut();
                }
            }
        });

        iouRecycler.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 && fab.isShown())
                    fab.hide();
                else if (dy < 0 && !fab.isShown())
                    fab.show();
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        // Run this on the main thread so this activity doesn't show
        if(Globals.authObject != null && Globals.authObject.isValid()){
            //Log.e("MAIN", "Auth is valid!");

            // Create a user object
            Globals.mainUser = new User(Globals.authObject.getUsername());
            usernameLabel.setText(Globals.mainUser.getUsername());
            fullNameLabel.setText(Globals.mainUser.getFullName());
            // Get their notifications (asynchronously)
            new GetNotificationsTask().execute();

            //TextView usernameLabel = (TextView) findViewById(R.id.username_label);
            //usernameLabel.setText(Globals.authObject.getUsername());
        }
        else {

            Globals.authObject = new OAuthObject(this);
            Globals.authObject.authorize(false);
            if(!Globals.authObject.isValid()){
                Intent intent = new Intent(thisActivity, LoginActivity.class);
                startActivity(intent);
                ((Activity) thisActivity).finish();
            } else {
                // Create a user object
                Globals.mainUser = new User(Globals.authObject.getUsername());
                usernameLabel.setText(Globals.authObject.getUsername());


                // Get their notifications (asynchronously)
                new GetNotificationsTask().execute();

                // Get their active IOUs (asynchronously)
                new GetActiveIousTask().execute();
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplfiableIfStatement
        if (id == R.id.action_notifications) {
            drawer.openDrawer(Gravity.RIGHT);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        currentTask = id;

        if (id == R.id.nav_ious) {
            new GetActiveIousTask().execute();
        } else if (id == R.id.nav_pending) {
            new GetPendingIousTask().execute();
        } else if (id == R.id.nav_previous) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_create) {

        } else if (id == R.id.nav_edit_prof) {

        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Downloads notifications from the server
     */
    private class GetNotificationsTask extends AsyncTask<String, Void, String> {

        boolean notifGet = false;

        @Override
        protected String doInBackground(String... params) {

            notifGet = Globals.mainUser.getNotifications();

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(notifGet){
                addNotifications();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Deletes a notification from the server given a message id
     */
    private class DeleteNotificationsTask extends AsyncTask<Integer, Void, String> {

        boolean notifDelete = false;

        @Override
        protected String doInBackground(Integer ... params) {

            notifDelete = Globals.mainUser.deleteNotification(params[0]);

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            if(notifDelete){
                Log.e("NOTIF", "Notification was deleted");
            } else {
                Log.e("NOTIF", "Notification was not deleted");
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Gets the active IOUs from the server
     */
    private class GetActiveIousTask extends AsyncTask<Integer, Void, String> {

        boolean iousGet = false;

        @Override
        protected String doInBackground(Integer ... params) {

            Log.e("IOUS", "About to get active ious");

            iousGet = Globals.mainUser.getActiveTransactions();

            Log.e("IOUS", "Got active IOUS: " + Globals.mainUser.allIOUs.size());

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            iouRefreshLayout.setRefreshing(false);

            if(iousGet) {
                showIous();
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Gets the pending IOUs from the server
     */
    private class GetPendingIousTask extends AsyncTask<Integer, Void, String> {

        boolean iousGet = false;

        @Override
        protected String doInBackground(Integer ... params) {

            Log.e("IOUS", "About to get pending ious");

            iousGet = Globals.mainUser.getPendingTransactions();

            Log.e("IOUS", "Got pending IOUS: " + Globals.mainUser.allIOUs.size());

            return "Success";

        }

        @Override
        protected void onPostExecute(String result) {

            iouRefreshLayout.setRefreshing(false);

            if(iousGet) {
                showIous();
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Shows the IOU in the main adapter
     */
    public void showIous(){

        transactionAdapter = new TransactionsAdapter(Globals.mainUser.allIOUs, R.layout.active_iou_view);
        iouRecycler.setAdapter(transactionAdapter);

    }

    /**
     * Load notifications in the notification pane
     */
    public void addNotifications(){

        notificationAdapter = new NotificationAdapter(Globals.mainUser.notifications);
        navigationViewRight.setAdapter(notificationAdapter);

    }

    /**
     * Performs actions required to log out the user
     */
    public void signOut(){
        if(Globals.authObject != null){
            Globals.authObject.delete(this);
        }
        Globals.authObject = null;
        Globals.mainUser = null;
        Intent intent = new Intent(thisActivity, LoginActivity.class);
        startActivity(intent);
        ((Activity) thisActivity).finish();
    }

    /**
     * Starts the activity to create a new IOU
     */
    public void startCreateActivity(){
        Intent createIntent = new Intent(thisActivity, CreateIOU.class);
        startActivity(createIntent);
    }

}
