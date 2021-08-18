package com.mobdeve.s13.group2.financify.cashflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobdeve.s13.group2.financify.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This activity serves as the "specific account view" when the user navigates to a specific
 * account entry.
 */
public class CashflowAccountActivity extends AppCompatActivity {

    // RecyclerView attributes
    private RecyclerView rvTransList;
    private LinearLayoutManager transManager;
    private TransactionAdapter transAdapter;
    private ArrayList<Transaction> transactions;

    // TextView attributes
    private TextView tvAccountName, tvAccountType, tvBalance, tvEmptyMessage;
    // ImageView to be utilized as a Button
    private ImageView ivEditBtn;
    // Account to be displayed in the activity
    private Account account;

    /* Filter attributes */
    private ImageButton ibTransFilterBtn;
    private ConstraintLayout clFilterContainer;
    private Spinner spTransType;
    private Button btnMonth, btnYear, btnClearFilter;
    private DatePickerDialog dpMonthDialog, dpYearDialog;
    private ArrayList<Transaction> transactionsBackup;
    private boolean filterVisible;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow specific account layout
        setContentView (R.layout.activity_cashflow_account_view);

        // Initialize RecyclerView components
        this.initRecyclerView ();

        // Initialize general information seen in this activity
        this.initInfo ();

        // Initialize filter components
        this.initFilters ();
    }

    /**
     * Show/hide empty message on activity resumption, if applicable
     */
    @Override
    protected void onResume () {
        super.onResume ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    /**
     * Initializes all the RecyclerView components.
     */
    private void initRecyclerView () {
        // Instantiate list
        this.transactions = new ArrayList<> ();

        // Initialize RecyclerView for transactions
        this.rvTransList = findViewById (R.id.rv_transactions);

        // Instantiate & Attach LayoutManager
        this.transManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvTransList.setLayoutManager (transManager);

        // TODO: TEMPORARY; Delete when db is implemented!
        this.initData ();
        transactionsBackup = new ArrayList<> (transactions);

        // Instantiate & Attach Adapter
        transAdapter = new TransactionAdapter (transactions);
        this.rvTransList.setAdapter (transAdapter);

        // Empty message for RecyclerView
        this.tvEmptyMessage = findViewById (R.id.tv_cf_entry_empty);
        this.tvEmptyMessage.setVisibility (View.GONE);
    }

    // TODO: TEMPORARY; Delete when db is implemented!
    private void initData () {
        this.transactions.add (new Transaction ("Paid tuition fee", 63000, Transaction.TYPE_EXPENSE, "01/01/2021"));
        this.transactions.add (new Transaction ("Monthly income", 100000, Transaction.TYPE_INCOME, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));

        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Stocks", 20000, Transaction.TYPE_INVESTMENT, "02/01/2019"));
        this.transactions.add (new Transaction ("Happy Birthday!", 20000, Transaction.TYPE_EXPENSE, "02/12/2021"));
        this.transactions.add (new Transaction ("Invested in Forex", 20000, Transaction.TYPE_INVESTMENT, "03/01/2016"));
    }

    /**
     * Initialize general components needed for filter feature for this activity.
     */
    private void initFilters () {
        // Retrieve element IDs
        this.ibTransFilterBtn = findViewById (R.id.ib_cashflow_entry_filter);
        this.btnClearFilter = findViewById (R.id.btn_cf_entry_clear_filter);
        this.clFilterContainer = findViewById (R.id.cl_cf_account_filter);

        // Default values
        this.filterVisible = false;
        this.clFilterContainer.setVisibility (View.GONE);

        // Initialize other components
        this.initSpinner ();
        this.initDatePickers ();

        // Toggle visibility of filters
        this.ibTransFilterBtn.setOnClickListener (new View.OnClickListener () {
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
        this.btnClearFilter.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                clearFilters ();
            }
        });
    }

    /**
     * Initializes all general information for this activity.
     */
    private void initInfo () {
        // Retrieve element IDs
        this.tvAccountName = findViewById (R.id.tv_specific_account_name);
        this.tvAccountType = findViewById (R.id.tv_specific_account_type);
        this.tvBalance = findViewById (R.id.tv_specific_account_balance);
        this.ivEditBtn = findViewById (R.id.iv_cashflow_edit_account);

        // Retrieve essential information passed from the homepage activity
        Intent i = getIntent ();

        // Instantiate Account for this activity
        account = new Account (i.getStringExtra (Keys.KEY_ID),
                i.getStringExtra (Keys.KEY_NAME),
                i.getFloatExtra (Keys.KEY_BAL, 0),
                i.getStringExtra (Keys.KEY_TYPE));

        // OnClickListener for editing Account information
        ivEditBtn.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowAccountActivity.this, CashflowUpdateAccountActivity.class);

                i.putExtra (Keys.KEY_ID, account.getId ());
                i.putExtra (Keys.KEY_NAME, account.getName ());
                i.putExtra (Keys.KEY_BAL, account.getBalance());
                i.putExtra (Keys.KEY_TYPE, account.getType ());

                startActivity (i);
            }
        });

        // Set value of TextViews
        tvAccountName.setText (account.getName ());
        tvAccountType.setText (account.getType ());
        tvBalance.setText (account.getBalanceFormatted ());
    }

    /**
     * Initialize all "Type" Spinner-related components.
     */
    private void initSpinner () {
        // Retrieve element ID
        this.spTransType = findViewById (R.id.sp_cf_type_filter);

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
        this.btnMonth = findViewById (R.id.btn_cf_month_filter);
        this.btnYear = findViewById (R.id.btn_cf_year_filter);

        // TODO: Figure out how to work with depreciated stuffs!
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
        this.btnMonth.setText (String.valueOf("NONE"));
        this.btnYear.setText (String.valueOf("NONE"));
        this.spTransType.setSelection (0);

        // Clear filtered list
        transactions.clear ();
        // Restore original list
        transactions.addAll (transactionsBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        transAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    /**
     * Filter. Filters the RecyclerView contents based on parameters set by the user.
     */
    private void filterTransactions () {
        // Retrieve filer from each setting
        String typeFilter = this.spTransType.getSelectedItem ().toString ().toLowerCase ();
        String monthFilter = this.btnMonth.getText ().toString ().toLowerCase ();
        String yearFilter = this.btnYear.getText ().toString ().toLowerCase ();

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
        if (!typeFilter.equalsIgnoreCase ("none")) {
            // Loop through each Transaction
            for (Transaction transaction : transactions) {
                // If "Type" filter matches Transaction type
                if (typeFilter.equalsIgnoreCase (transaction.getType ()))
                    // Add to temporary list
                    temp.add (transaction);
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
        if (transactions.size () == 0)
            this.tvEmptyMessage.setVisibility (View.VISIBLE);
        else
            this.tvEmptyMessage.setVisibility (View.GONE);
    }
}