package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s13.group2.financify.cashflow.CashflowHomeActivity;
import com.mobdeve.s13.group2.financify.reminders.RemindersActivity;
import com.mobdeve.s13.group2.financify.summary.SummaryActivity;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Objects;

/**
 * For application's header and navigation bar. Parent activity for all activities in the
 * application to show the header and navigation bar containing the application's features and
 * functionalities.
 */
public class BaseActivity extends AppCompatActivity {

    // SharedPreferences to get logged in user's firstname and lastname
    private SharedPreferences sharedPreferences;

    // layout resource id of current activity extending BaseActivity
    private int layoutResId;

    // delay in milliseconds to close application processes
    private static final int DELAY = 500;

    // current activity on screen's layout
    private FrameLayout flActivity;

    // navigation view bar
    private NavigationView navigationView;

    // drawer layout
    private DrawerLayout drawerLayout;

    // drawer menu
    private Menu drawerMenu;

    // toolbar (application header)
    Toolbar toolbar;

    // hamburger menu (toggle) for navigation view bar
    ActionBarDrawerToggle toggle;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        // get view elements
        this.flActivity = findViewById(R.id.fl_activity);
        this.navigationView = findViewById(R.id.nav_view);
        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // to remove default title in toolbar (action bar)
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // initialize drawer layout and navigation view bar menu items
        initDrawer();
    }

    /**
     * Initializes the navigation bar.
     */
    public void initDrawer() {
        // get header child layout from navigation view layout
        View navHeader = this.navigationView.getHeaderView(0);
        // get textview from header child layout
        TextView tvNavHeader = navHeader.findViewById(R.id.tv_nav_header);
        // get first name and last name of user from SharedPreferences
        this.sharedPreferences = getSharedPreferences("financify", Context.MODE_PRIVATE);
        String firstName = this.sharedPreferences.getString("FIRSTNAME", "");
        String lastName = this.sharedPreferences.getString("LASTNAME", "");
        String fullName = firstName + " " + lastName;
        // set user's full name to the navigation view's header
        tvNavHeader.setText(fullName);

        // set toggle for navigation bar
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close
        );
        drawerLayout.addDrawerListener(toggle);

        // set listener for menu items in the navigation view bar
        drawerMenu = navigationView.getMenu();
        for (int j = 0; j < drawerMenu.size(); j++) {
            drawerMenu.getItem(j).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // TODO: complete code here
                    // intent for redirecting to other activities according to menu item pressed
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            // launch main home activity
                            launchActivity(R.layout.activity_home, HomeActivity.class);
                            return true;

                        case R.id.nav_summary:
                            // launch summary activity
                            launchActivity(R.layout.activity_summary, SummaryActivity.class);
                            return true;

                        case R.id.nav_cash_flow:
                            // launch cash flow home activity
                            launchActivity(R.layout.activity_cashflow_homepage, CashflowHomeActivity.class);
                            return true;

                        case R.id.nav_reminder_list:
                            // launch reminder list home activity
                            launchActivity(R.layout.activity_main_reminders, RemindersActivity.class);
                            return true;

                        case R.id.nav_settings:
                            // launch settings activity
                            launchActivity(R.layout.activity_settings, SettingsActivity.class);
                            return true;

                        case R.id.nav_logout:
                            // sign out current user of the application
                            FirebaseAuth.getInstance().signOut();

                            // redirect back to login page
                            intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);

                            // end current activity
                            finish();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Launches the activity of the chosen feature on the application's navigation bar.
     *
     * @param   layout      the activity's layout ID
     * @param   activity    the activity's class
     * @param   <T>         represents any activity class only
     */
    private <T> void launchActivity (int layout, Class<T> activity) {
        // check if user is not currently on chosen activity from menu
        if (this.layoutResId != layout) {
            // redirect to chosen activity
            Intent intent = new Intent(getBaseContext(), activity);
            startActivity(intent);

            // end current activity
            finish();
        } else {
            // display message to indicate that user is already on the activity
            Toast.makeText(
                    getBaseContext(),
                    "You are already on the page.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    // override setContentView to put current activity layout as child layout
    // inside FrameLayout of base layout (activity_base.xml)
    @Override
    public void setContentView(int layoutResID) {
        if (flActivity != null) {
            // store layout resource id
            this.layoutResId = layoutResID;
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View activity = inflater.inflate(layoutResID, flActivity, false);
            flActivity.addView(activity, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (flActivity != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            flActivity.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (flActivity != null) {
            flActivity.addView(view, params);
        }
    }

    @Override
    public void onBackPressed() {
        // when back button is pressed and navigation view bar is opened, close
        // the navigation view bar
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // if current activity is the only activity on state
        else if (isTaskRoot()) {
            // end and close application and its tasks
            finishAndRemoveTask();

            // kill application process when activity is finished completely
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, DELAY);
        }
        else {
            // move back to previous activity
            super.onBackPressed();
        }
    }
}