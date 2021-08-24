package com.mobdeve.s13.group2.financify.cashflow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;
import com.mobdeve.s13.group2.financify.model.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This activity serves as the "specific account view" when the user navigates to a specific
 * account entry.
 */
public class CashflowAccountActivity extends BaseActivity {

    // RecyclerView Attributes
    private RecyclerView rvTransList;
    private LinearLayoutManager transManager;
    private TransactionAdapter transAdapter;
    private ArrayList<Transaction> transactions;

    // UI Attributes
    private TextView tvAccountName, tvAccountType, tvBalance, tvEmptyMessage;
    private ImageView ivEditBtn;
    private ConstraintLayout clHomeBtn;
    private ProgressBar pbAccount;

    // Account to be displayed in the activity
    private Account account;

    // Filter Attributes
    private ImageButton ibTransFilterBtn;
    private ConstraintLayout clFilterContainer;
    private Spinner spTransType;
    private Button btnMonth, btnYear, btnClearFilter;
    private DatePickerDialog dpMonthDialog, dpYearDialog;
    private ArrayList<Transaction> transactionsBackup;
    private boolean filterVisible;

    // Firebase Attributes
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference dbRef;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow specific account layout
        setContentView (R.layout.activity_cashflow_account_view);

        // Initialize general information seen in this activity
        initInfo ();
        // Initialize Firebase components
        initFirebase ();
        // Initialize RecyclerView components
        initRecyclerView ();
        // Initialize filter components
        initFilters ();
    }

    /**
     * Show/hide empty message on activity resumption, if applicable.
     */
    @Override
    protected void onResume () {
        super.onResume ();

        // Show empty message, if applicable
        displayEmptyMessage ();
    }

    /**
     * When the user presses the back button of the device.
     */
    @Override
    public void onBackPressed () {
        // Go back to homepage
        goBackToHomepage ();
    }

    /**
     * Launches an activity leading to the Cashflow Homepage and finishes this activity.
     */
    private void goBackToHomepage () {
        Intent i = new Intent (CashflowAccountActivity.this, CashflowHomeActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ();

        // If valid session
        if (user != null) {
            // Retrieve user ID
            userId = user.getUid ();
            // Create DB reference to a specific Cashflow account of this user
            dbRef = FirebaseDatabase.getInstance ().getReference ().child ("users")
                    .child (userId)
                    .child ("accounts")
                    .child (account.getId ());

            // Value listener for this DB reference
            dbRef.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange (@NonNull @NotNull DataSnapshot snapshot) {
                    // Temporarily empty Transactions
                    transactions.clear ();

                    // To catch NullPointerException
                    try {
                        // Temporary array to store Transactions retrieved from Firebase
                        ArrayList<Transaction> transFromFirebase = new ArrayList<> ();

                        // Loop through received Transactions
                        for (DataSnapshot transaction : snapshot.child ("transactions").getChildren ()) {
                            // Instantiate a Transaction object per child
                            transFromFirebase.add (new Transaction (
                                    transaction.getKey (),
                                    transaction.child ("description").getValue ().toString (),
                                    Double.valueOf (transaction.child ("amount").getValue ().toString ()),
                                    transaction.child ("type").getValue ().toString (),
                                    transaction.child ("date").getValue ().toString (),
                                    transaction.child ("accountId").getValue ().toString ()
                            ));
                        }

                        // Instantiate Account to be used in this activity
                        account = new Account (
                            snapshot.getKey (),
                            snapshot.child ("name").getValue ().toString (),
                            Double.parseDouble (snapshot.child ("balance").getValue ().toString ()),
                            snapshot.child ("type").getValue ().toString (),
                            transFromFirebase
                        );
                    } catch (Exception e) {
                        System.out.println (e.toString ());
                    }

                    // Once Firebase operations are done, initialize local attributes
                    // Populate Transaction list
                    transactions.addAll (account.getTransactions ());
                    // Create a backup list (for filter feature)
                    transactionsBackup = new ArrayList<> (transactions);
                    // Hide ProgressBar
                    pbAccount.setVisibility (View.GONE);
                    // Refresh the Adapter
                    transAdapter.notifyDataSetChanged ();
                    // Display empty message, if applicable
                    displayEmptyMessage ();

                    // Update Account balance being displayed
                    tvBalance.setText (account.getBalanceFormatted ());
                }

                @Override
                public void onCancelled (@NonNull @NotNull DatabaseError error) {}
            });
        // If invalid session
        } else {
            // TODO: Handle when session is invalid.
        }
    }

    /**
     * Initializes all the RecyclerView components.
     */
    private void initRecyclerView () {
        // Initialize RecyclerView for transactions
        rvTransList = findViewById (R.id.rv_transactions);

        // Instantiate & Attach LayoutManager
        transManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTransList.setLayoutManager (transManager);

        // Instantiate & Attach Adapter
        transAdapter = new TransactionAdapter (account, transactions);
        rvTransList.setAdapter (transAdapter);

        // Empty message for RecyclerView
        tvEmptyMessage = findViewById (R.id.tv_cf_entry_empty);
        tvEmptyMessage.setVisibility (View.GONE);
    }

    /**
     * Initializes all general information for this activity.
     */
    private void initInfo () {
        // Retrieve element IDs
        tvAccountName = findViewById (R.id.tv_specific_account_name);
        tvAccountType = findViewById (R.id.tv_specific_account_type);
        tvBalance = findViewById (R.id.tv_specific_account_balance);
        ivEditBtn = findViewById (R.id.iv_cashflow_edit_account);
        clHomeBtn = findViewById (R.id.cl_cf_back_home_nav);
        pbAccount = findViewById (R.id.pb_cf_account);

        // Back button for Account page
        clHomeBtn.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToHomepage ();
            }
        });

        // Retrieve essential information passed from the homepage activity
        Intent i = getIntent ();

        // Instantiate Account for this activity
        account = i.getParcelableExtra (Keys.KEY_ACC);
        transactions = new ArrayList<> (account.getTransactions ());
        transactionsBackup = new ArrayList<> (transactions);

        // OnClickListener for editing Account information
        ivEditBtn.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowAccountActivity.this, CashflowUpdateAccountActivity.class);

                i.putExtra (Keys.KEY_ACC, account);

                startActivity (i);
                finish ();
            }
        });

        // Set value of TextViews
        tvAccountName.setText (account.getName ());
        tvAccountType.setText (account.getType ());
    }

    /**
     * Initialize general components needed for filter feature for this activity.
     */
    private void initFilters () {
        // Retrieve element IDs
        ibTransFilterBtn = findViewById (R.id.ib_cashflow_entry_filter);
        btnClearFilter = findViewById (R.id.btn_cf_entry_clear_filter);
        clFilterContainer = findViewById (R.id.cl_cf_account_filter);

        // Default values
        filterVisible = false;
        clFilterContainer.setVisibility (View.GONE);

        // Initialize other components
        initSpinner ();
        initDatePickers ();

        // Toggle visibility of filters
        ibTransFilterBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (filterVisible) {
                    clFilterContainer.setVisibility(View.GONE);
                } else {
                    clFilterContainer.setVisibility(View.VISIBLE);
                }

                filterVisible = !filterVisible;
            }
        });

        // OnClickListener for Button that clears all filters
        btnClearFilter.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                clearFilters ();
            }
        });
    }

    /**
     * Initialize all "Type" Spinner-related components.
     */
    private void initSpinner () {
        // Retrieve element ID
        spTransType = findViewById (R.id.sp_cf_type_filter);

        // Initialize "Type" Spinner
        ArrayAdapter<CharSequence> spTypeFilterAdapter = ArrayAdapter.createFromResource (
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );
        spTypeFilterAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spTransType.setAdapter (spTypeFilterAdapter);

        // "Type" onItemSelectedListener (to trigger filtering of Transactions)
        spTransType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTransactions ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Initialize all DatePickerDialog-related components.
     */
    private void initDatePickers () {
        // Retrieve element IDs
        btnMonth = findViewById (R.id.btn_cf_month_filter);
        btnYear = findViewById (R.id.btn_cf_year_filter);

        // TODO: Figure out how to work with deprecated stuffs!
        System.out.println ("VERSION: " + android.os.Build.VERSION.SDK_INT);

        // For retrieving date today
        Calendar cal = Calendar.getInstance ();

        /* Month DatePickerDialog components */
        // Initialize Month DatePickerDialog (Filter for Month)
        dpMonthDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_DialogWhenLarge, new DatePickerDialog.OnDateSetListener() {
            // On selecting a month, trigger filter
            @Override
            public void onDateSet (DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnMonth.setText (DateHelper.getMonthFormat (month + 1));
                // Trigger filter
                filterTransactions ();
            }
        }, cal.get (Calendar.YEAR), cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH)) {
            // For styling purposes only, removes additional background design
            @Override
            public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
            }
        };

        // To remove "Day" and "Year" inputs of DatePickerDialog for Month
        dpMonthDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dpMonthDialog.getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE);

        // OnClickListener for Button (to show DatePickerDialog for Month)
        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dpMonthDialog.show ();
            }
        });

        /* Year DatePickerDialog components */
        // Initialize Year DatePickerDialog (Filter for Year)
        dpYearDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_DialogWhenLarge, new DatePickerDialog.OnDateSetListener() {
            // On selecting a year, trigger filter
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnYear.setText (String.valueOf (year));
                // Trigger filter
                filterTransactions ();
            }
        }, cal.get (Calendar.YEAR), cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH)) {
            // For styling purposes only, removes additional background design
            @Override
            public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
            }
        };

        // To remove "Day" and "Month" inputs of DatePickerDialog for Year
        dpYearDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dpYearDialog.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);

        // OnClickListener for Button (to show DatePickerDialog for Year)
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dpYearDialog.show ();
            }
        });
    }

    /**
     * Clear the filter. Resets the RecyclerView contents to its default state.
     */
    private void clearFilters () {
        btnMonth.setText (String.valueOf("NONE"));
        btnYear.setText (String.valueOf("NONE"));
        spTransType.setSelection (0);

        // Clear filtered list
        transactions.clear ();
        // Restore original list
        transactions.addAll (transactionsBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        transAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        displayEmptyMessage ();
    }

    /**
     * Filter. Filters the RecyclerView contents based on parameters set by the user.
     */
    private void filterTransactions () {
        // Retrieve filer from each setting
        String typeFilter = spTransType.getSelectedItem ().toString ().toLowerCase ();
        String monthFilter = btnMonth.getText ().toString ().toLowerCase ();
        String yearFilter = btnYear.getText ().toString ().toLowerCase ();

        /*
            GENERAL IDEA FOR FILTERING:
            (1) Filter main list based on TYPE first, store filtered content into a temporary list,
                replace main list contents with temporary list contents, and proceed to next filter.
            (2) Repeat for Month and Year filter.
            (3) At the end, the main list will contain elements that passed through all filters.
         */

        // Clear filtered list to avoid potential duplication
        transactions.clear ();
        // Restore original list
        transactions.addAll (transactionsBackup);
        // Create a backup of the original list
        transactionsBackup = new ArrayList<> (transactions);

        // Create a temporary list (for copying purposes)
        ArrayList<Transaction> temp = new ArrayList<> ();

        // If the user filtered based on TRANSACTION TYPE
        if (!typeFilter.equalsIgnoreCase ("Select type…")) {
            // Loop through each Transaction
            for (Transaction transaction : transactions) {
                // If "Type" filter matches Transaction type
                if (typeFilter.equalsIgnoreCase (transaction.getType ())) {
                    // Add to temporary list
                    temp.add(transaction);
                }
            }

            // Reset main list
            transactions.clear ();
            // Populate main list with filtered content
            transactions.addAll (temp);
            // Clear filtered list holder
            temp.clear ();


        // If the user resets the "Type" filter back to "none"
        } else {
            // Clear the main list, and restore from backup list
            transactions.clear ();
            transactions.addAll (transactionsBackup);
        }

        // If the user filtered based on the MONTH of Transactions
        if (!monthFilter.equalsIgnoreCase ("none")) {
            // Loop through each Transaction
            for (Transaction transaction : transactions) {
                // If "Month" filter matches the Month of the Transaction
                if (monthFilter.equalsIgnoreCase (DateHelper.getMonthFormat (Integer.parseInt (transaction.getMonth ()))))
                    // Add to temporary list
                    temp.add (transaction);
            }

            // Reset main list
            transactions.clear ();
            // Populate main list with filtered content
            transactions.addAll (temp);
            // Clear filtered list holder
            temp.clear ();
        }

        // If the user filtered based on the YEAR of Transactions
        if (!yearFilter.equalsIgnoreCase ("none")) {
            // Loop through each Transaction
            for (Transaction transaction : transactions) {
                // If "Year" filter matches the Year of the Transaction
                if (yearFilter.equalsIgnoreCase (transaction.getYear ()))
                    // Add to temporary list
                    temp.add (transaction);
            }

            // Reset main list
            transactions.clear ();
            // Populate main list with filtered content
            transactions.addAll (temp);
            // Clear filtered list holder
            temp.clear ();
        }

        // Refresh RecyclerView
        transAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    /**
     * Shows and hides the empty message for the RecyclerView.
     */
    private void displayEmptyMessage () {
        if (transactions.isEmpty ())
            tvEmptyMessage.setVisibility (View.VISIBLE);
        else
            tvEmptyMessage.setVisibility (View.GONE);
    }
}