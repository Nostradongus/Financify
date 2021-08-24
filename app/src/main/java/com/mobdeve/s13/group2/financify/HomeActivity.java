package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s13.group2.financify.cashflow.CashflowHomeActivity;
import com.mobdeve.s13.group2.financify.summary.SummaryActivity;

// TODO: add documentation
public class HomeActivity extends BaseActivity {

    /**
     * SharedPreferences for stored user firstname and lastname.
     */
    private SharedPreferences sharedPreferences;

    /**
     * User greeting text.
     */
    private TextView tvUserGreeting;

    /**
     * Summary layout button.
     */
    private ConstraintLayout clSummary;
    /**
     * Cash flow layout button.
     */
    private ConstraintLayout clCashFlow;
    /**
     * Reminder list layout button.
     */
    private ConstraintLayout clReminderList;
    /**
     * Budget sheet layout button.
     */
    private ConstraintLayout clBudgetSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize components
        initComponents();
    }

    private void initComponents() {
        // initialize components
        this.tvUserGreeting = findViewById(R.id.tv_user_greeting);
        this.clSummary = findViewById(R.id.cl_summary_card);
        this.clCashFlow = findViewById(R.id.cl_cash_flow_card);
//        this.clReminderList = findViewById(R.id.cl_reminder_list_card);
//        this.clBudgetSheet = findViewById(R.id.cl_budget_sheet_card);

        // update greeting with logged in user's first name
        this.sharedPreferences = getSharedPreferences("financify", Context.MODE_PRIVATE);
        String firstName = this.sharedPreferences.getString("FIRSTNAME", "");
        String userGreeting = "Hello, " + firstName + "!";
        tvUserGreeting.setText(userGreeting);

        // TODO: add and complete the functionalities to the rest of the image buttons
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
    }
}