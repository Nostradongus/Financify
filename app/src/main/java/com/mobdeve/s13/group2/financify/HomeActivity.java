package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobdeve.s13.group2.financify.cashflow.CashflowHomeActivity;
import com.mobdeve.s13.group2.financify.reminders.RemindersActivity;
import com.mobdeve.s13.group2.financify.summary.SummaryActivity;

/**
 * For home activity / page, the application's home page after the user logs in.
 */
public class HomeActivity extends BaseActivity {

    // SharedPreferences for stored first and last name of user.
    private SharedPreferences sharedPreferences;

    // user greeting text
    private TextView tvUserGreeting;

    // summary card button
    private ConstraintLayout clSummary;

    // cash flow card button
    private ConstraintLayout clCashFlow;

    // reminder list card button
    private ConstraintLayout clReminderList;

    // account settings card button
    private ConstraintLayout clSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize components
        initComponents();
    }

    /**
     * Initializes UI components.
     */
    private void initComponents() {
        // initialize components
        this.tvUserGreeting = findViewById(R.id.tv_user_greeting);
        this.clSummary = findViewById(R.id.cl_summary_card);
        this.clCashFlow = findViewById(R.id.cl_cash_flow_card);
        this.clReminderList = findViewById(R.id.cl_reminder_list_card);
        this.clSettings = findViewById (R.id.cl_settings_card);

        // update greeting with logged in user's first name
        this.sharedPreferences = getSharedPreferences("financify", Context.MODE_PRIVATE);
        String firstName = this.sharedPreferences.getString("FIRSTNAME", "");
        String userGreeting = "Hello, " + firstName + "!";
        tvUserGreeting.setText(userGreeting);

        // set listener for summary card button
        this.clSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to cash flow's start activity
                Intent intent = new Intent(getBaseContext(), SummaryActivity.class);
                startActivity(intent);

                // end current activity
                finish();
            }
        });

        // set listener for cash flow card button
        this.clCashFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to cash flow's start activity
                Intent intent = new Intent(getBaseContext(), CashflowHomeActivity.class);
                startActivity(intent);

                // end current activity
                finish();
            }
        });

        // set listener for reminder list card button
        this.clReminderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to reminder list's start activity
                Intent intent = new Intent(getBaseContext(), RemindersActivity.class);
                startActivity(intent);

                // end current activity
                finish();
            }
        });

        // set listener for settings card button
        this.clSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to settings' start activity
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);

                // end current activity
                finish();
            }
        });
    }
}