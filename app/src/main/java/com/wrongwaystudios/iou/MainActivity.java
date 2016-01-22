package com.wrongwaystudios.iou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wrongwaystudios.iou.resources.User;
import com.wrongwaystudios.iou.resources.UserNotification;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context thisActivity = this;
    private DrawerLayout drawer = null;

    private TextView usernameLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        View header = navigationViewLeft.getHeaderView(0);
        usernameLabel = (TextView) header.findViewById(R.id.username_label);
        navigationViewLeft.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart(){
        super.onStart();

        // Run this on the main thread so this activity doesn't show
        if(Globals.authObject != null && Globals.authObject.isValid()){
            //Log.e("MAIN", "Auth is valid!");

            // Create a user object
            Globals.mainUser = new User(Globals.authObject.getUsername());
            usernameLabel.setText(Globals.authObject.getUsername());

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
        if (id == R.id.action_signout) {
            signOut();
            return true;
        }
        else if (id == R.id.action_notifications) {
            drawer.openDrawer(Gravity.RIGHT);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ious) {
            // Handle the camera action
        } else if (id == R.id.nav_pending) {

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
            }

        }

        @Override
        protected void onPreExecute() {



        }

    }

    /**
     * Load notifications in the notification pane
     */
    public void addNotifications(){

        RecyclerView navigationViewRight = (RecyclerView) findViewById(R.id.recycler_view_right);
        navigationViewRight.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        navigationViewRight.setLayoutManager(layoutManager);

        NotificationAdapter adapter = new NotificationAdapter(Globals.mainUser.notifications);
        navigationViewRight.setAdapter(adapter);

        for(UserNotification notif : Globals.mainUser.notifications){

            View child = getLayoutInflater().inflate(R.layout.notification_view, null);
            ((TextView) child.findViewById(R.id.notification_label)).setText(notif.getMessage());
            child.findViewById(R.id.notification_ind).setVisibility(View.VISIBLE);

            navigationViewRight.addView(child);

        }

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

}
