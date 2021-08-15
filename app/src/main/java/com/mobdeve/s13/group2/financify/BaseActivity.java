package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

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

    public void initDrawer() {
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
                    switch (item.getItemId()) {
                        case R.id.nav_portfolio:

                            return true;
                        case R.id.nav_cash_flow:

                            return true;
                        case R.id.nav_reminder_list:

                            return true;
                        case R.id.nav_settings:

                            return true;
                        case R.id.nav_logout:

                            return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

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
        } else {
            super.onBackPressed();
        }
    }
}