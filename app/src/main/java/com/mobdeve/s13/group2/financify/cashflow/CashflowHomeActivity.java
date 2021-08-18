package com.mobdeve.s13.group2.financify.cashflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobdeve.s13.group2.financify.R;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This activity serves as the "landing page" when a user navigates to the Cash Flow section of
 * the mobile application.
 */
public class CashflowHomeActivity extends AppCompatActivity {

    /* RecyclerView attributes */
    private RecyclerView rvAccountList;
    private LinearLayoutManager accountManager;
    private AccountAdapter accountAdapter;
    private ArrayList<Account> accounts;

    /* FloatingActionButton attributes */
    // FloatingActionButtons for various add functionalities
    private FloatingActionButton fabAddMain, fabAddEntry, fabAddAccount;
    // Boolean that represents if the smaller FloatingActionButtons will be shown.
    private boolean fabShow;
    // Elements for the FloatingActionButton implementation
    private ConstraintLayout clAddEntry, clAddAccount;
    private TextView tvAddEntry, tvAddAccount;

    /* TextView for Current Balance */
    private TextView tvCurrBal;

    /* TextView for empty message */
    private TextView tvEmptyMessage;

    /* Filter & SearchView attributes */
    private ImageButton ibAccountFilterBtn;
    private SearchView svAccountSearch;
    private boolean filterVisible;
    private ArrayList<Account> accountsBackup;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow homepage layout
        setContentView (R.layout.activity_cashflow_homepage);

        // Initialize RecyclerView components
        this.initRecyclerView ();
        // Initialize FloatingActionButton components
        this.initFABs ();
        // Initialize SearchView components
        this.initSearchView ();
        // Initialize & Set Current Balance TextView value
        this.initCurrBal ();
    }

    /**
     * Hide smaller FloatingActionButtons when leaving this activity.
     */
    @Override
    protected void onPause () {
        super.onPause ();

        closeFABMenu ();
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
     * Initializes the "current balance" display.
     */
    private void initCurrBal () {
        // Retrieve element ID
        this.tvCurrBal = findViewById (R.id.tv_cashflow_current_balance);

        // Set value
        this.tvCurrBal.setText (NumberFormat.getCurrencyInstance ().format (this.computeCurrentBalance ()));
    }

    /**
     * Initializes all the RecyclerView components.
     */
    private void initRecyclerView () {
        // Instantiate accounts ArrayList
        this.accounts = new ArrayList<> ();

        // Initialize RecyclerView for accounts
        this.rvAccountList = findViewById (R.id.rv_accounts);

        // Instantiate & Attach LayoutManager
        this.accountManager = new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false);
        this.rvAccountList.setLayoutManager (accountManager);

        // TODO: TEMPORARY; Delete when db is implemented!
        this.initData ();

        // Instantiate & Attach Adapter
        accountAdapter = new AccountAdapter (accounts);
        this.rvAccountList.setAdapter (accountAdapter);

        // Empty message for RecyclerView
        this.tvEmptyMessage = findViewById (R.id.tv_cf_account_empty);
        this.tvEmptyMessage.setVisibility (View.GONE);
    }

    // TODO: TEMPORARY; Delete when db is implemented!
    private void initData () {
        this.accounts.add (new Account ("1", "My Physical Wallet", 1000, Account.TYPE_PHYSICAL));
        this.accounts.add (new Account ("2", "My BDO Account", 30000, Account.TYPE_BANK));
        this.accounts.add (new Account ("3", "My Shopee Wallet", 5040, Account.TYPE_DIGITAL));
        this.accounts.add (new Account ("4", "My GCash", 5040, Account.TYPE_DIGITAL));
        this.accounts.add (new Account ("5", "My EastWest Account", 30000, Account.TYPE_BANK));
    }

    /**
     * Initialize all the SearchView-related components.
     */
    private void initSearchView () {
        // Filter settings is invisible by default
        this.filterVisible = false;

        // Initialize elements
        this.ibAccountFilterBtn = findViewById (R.id.ib_cashflow_account_filter);
        this.svAccountSearch = findViewById (R.id.sv_account_search);
        this.svAccountSearch.setVisibility (View.GONE);

        // To toggle the visibility of the SearchView
        this.ibAccountFilterBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (filterVisible) {
                    svAccountSearch.setVisibility(View.GONE);
                } else {
                    svAccountSearch.setVisibility(View.VISIBLE);
                }

                filterVisible = !filterVisible;
            }
        });

        // To make SearchView clickable from anywhere, not just on icon
        this.svAccountSearch.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                svAccountSearch.setIconified(false);
            }
        });

        // When queries are made through the SearchView
        this.svAccountSearch.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            // When query is submitted
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length () > 0)
                    searchAccounts (query);

                return false;
            }

            // When there is a change in text
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length () == 0 &&
                    accounts != null)
                    resetRecyclerViewContents ();

                return false;
            }
        });
    }

    /**
     * Update the contents of the accounts ArrayList based on SearchView query.
     *
     * @param text  the text that will be used for searching
     */
    private void searchAccounts (String text) {
        // Create a copy of accounts ArrayList
        accountsBackup = new ArrayList<> (accounts);

        // Empty accounts ArrayList
        accounts.clear ();

        // Add Accounts matching the query into the accounts ArrayList
        for (Account account : accountsBackup)
            if (account.getName ().toLowerCase ().contains (text.toLowerCase ()))
                accounts.add (account);

        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        accountAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    /**
     * Reset the contents of the accounts ArrayList.
     */
    private void resetRecyclerViewContents () {
        // Clear filtered list
        accounts.clear ();
        // Restore original list
        accounts.addAll (accountsBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        accountAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    /**
     * Initialize all the FloatingActionButton-related attributes.
     */
    private void initFABs () {
        // Smaller FABs are hidden by default
        this.fabShow = false;

        // Initialize elements
        this.fabAddMain = findViewById (R.id.fab_cashflow_add);
        this.fabAddEntry = findViewById (R.id.fab_cashflow_add_entry);
        this.fabAddAccount = findViewById (R.id.fab_cashflow_add_account);
        this.clAddEntry = findViewById (R.id.cl_add_entry);
        this.tvAddEntry = findViewById (R.id.tv_fab_add_entry_label);
        this.clAddAccount = findViewById (R.id.cl_add_account);
        this.tvAddAccount = findViewById (R.id.tv_fab_add_account_label);

        // OnClickListener for the MAIN FloatingActionButton
        this.fabAddMain.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (!fabShow)
                    showFABMenu ();
                else
                    closeFABMenu ();
            }
        });

        // OnClickListener for ADD TRANSACTION FloatingActionButton
        this.fabAddEntry.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowHomeActivity.this, CashflowAddEntryActivity.class);
                startActivity (i);
            }
        });

        // OnClickListener for UPDATE TRANSACTION FloatingActionButton
        this.fabAddAccount.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowHomeActivity.this, CashflowAddAccountActivity.class);
                // TODO: Pass information via putExtra () (TO BE DONE WHEN DATABASE IS FINAL)
                startActivity (i);
            }
        });
    }

    /**
     * Displays the smaller FloatingActionButtons when the main FAB button is pressed.
     */
    private void showFABMenu () {
        // To signify that the smaller FABs are visible
        this.fabShow = true;

        // Animate FABs
        this.clAddAccount.animate().translationY(-getResources().getDimension (R.dimen.standard_55));
        this.clAddEntry.animate().translationY(-getResources().getDimension (R.dimen.standard_105));

        // Show TextViews
        this.tvAddEntry.setVisibility (View.VISIBLE);
        this.tvAddAccount.setVisibility (View.VISIBLE);
    }

    /**
     * Hides the smaller FloatingActionButtons when the main FAB button is pressed.
     */
    private void closeFABMenu () {
        // To signify that the smaller FABs are invisible
        this.fabShow = false;

        // Hide TextViews
        this.tvAddEntry.setVisibility (View.GONE);
        this.tvAddAccount.setVisibility (View.GONE);

        // Animate FABs
        this.clAddAccount.animate().translationY(0);
        this.clAddEntry.animate().translationY(0);
    }

    /**
     * Computes and returns the current balance of the user based on all of their accounts.
     *
     * @return the current balance of the user
     */
    private float computeCurrentBalance () {
        float sum = 0;

        for (Account account : accounts) {
            sum += account.getBalance ();
        }

        return sum;
    }

    /**
     * Shows and hides the empty message for the RecyclerView.
     */
    private void displayEmptyMessage () {
        if (accounts.size () == 0)
            this.tvEmptyMessage.setVisibility (View.VISIBLE);
        else
            this.tvEmptyMessage.setVisibility (View.GONE);
    }
}