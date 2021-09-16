package com.mobdeve.s13.group2.financify.cashflow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;
import com.mobdeve.s13.group2.financify.model.Transaction;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This activity serves as the "landing page" when a user navigates to the Cash Flow section of
 * the mobile application.
 */
public class CashflowHomeActivity extends BaseActivity {

    // RecyclerView Attributes
    private RecyclerView rvAccountList;
    private LinearLayoutManager accountManager;
    private AccountAdapter accountAdapter;

    // List of Accounts
    private ArrayList<Account> accounts;

    // UI Attributes
    private ConstraintLayout clAddEntry, clAddAccount, clHomeBtn;
    private TextView tvAddEntry, tvAddAccount, tvCurrBal, tvEmptyMessage;
    private FloatingActionButton fabAddMain, fabAddEntry, fabAddAccount;
    private boolean fabShow;

    // Filter Attributes
    private ImageButton ibAccountFilterBtn;
    private SearchView svAccountSearch;
    private boolean filterVisible;
    private ArrayList<Account> accountsBackup;

    // Firebase Attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;
    private ProgressBar pbHome;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow homepage layout
        setContentView (R.layout.activity_cashflow_homepage);

        // Initialize General components
        initComponents ();
        // Initialize RecyclerView components
        initRecyclerView ();
        // Initialize FloatingActionButton components
        initFABs ();
        // Initialize SearchView components
        initSearchView ();
        // Initialize Firebase components
        initFirebase ();
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
     * Initialize general components.
     */
    private void initComponents () {
        clHomeBtn = findViewById (R.id.cl_cfh_back_home_nav);

        // Back button for Account page
        clHomeBtn.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (CashflowHomeActivity.this, HomeActivity.class);
                startActivity (i);
                finish ();
            }
        });
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        this.mAuth = FirebaseAuth.getInstance ();
        this.user = this.mAuth.getCurrentUser ();

        this.pbHome = findViewById (R.id.pb_cf_home);
        this.pbHome.setVisibility (View.VISIBLE);

        if (this.user != null) {
            // valid session
            this.userId = this.user.getUid ();
            dbRef = FirebaseDatabase.getInstance ().getReference ("users").child (this.userId);

            dbRef.addValueEventListener(new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    accounts.clear ();
                    for (DataSnapshot account : snapshot.child ("accounts").getChildren ()) {
                        try {
                            ArrayList<Transaction> transFromFirebase = new ArrayList<> ();

                            // String id, String desc, double amt, String type, String date, String accountId
                            for (DataSnapshot transaction : account.child ("transactions").getChildren ()) {
                                // Instantiate a Transaction object per child
                                transFromFirebase.add (new Transaction (
                                        transaction.getKey (),
                                        transaction.child ("description").getValue ().toString (),
                                        Double.parseDouble (transaction.child ("amount").getValue ().toString ()),
                                        transaction.child ("type").getValue ().toString (),
                                        transaction.child ("date").getValue ().toString (),
                                        transaction.child ("accountId").getValue ().toString ()
                                ));
                            }

                            accounts.add (new Account (
                                    account.getKey (),
                                    account.child ("name").getValue ().toString (),
                                    Double.parseDouble (account.child ("balance").getValue ().toString ()),
                                    account.child ("type").getValue ().toString (),
                                    transFromFirebase
                            ));

                        } catch (Exception e) {
                            System.out.println (e.toString ());
                        }
                    }

                    accountsBackup = new ArrayList<> (accounts);
                    pbHome.setVisibility (View.GONE);
                    accountAdapter.notifyDataSetChanged ();
                    displayEmptyMessage ();

                    // Initialize & Set Current Balance TextView value
                    initCurrBal ();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    pbHome.setVisibility (View.GONE);
                }
            });
        } else {
            // relogin
            Toast.makeText (CashflowHomeActivity.this, "Invalid session.", Toast.LENGTH_SHORT).show ();
        }
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

        // Instantiate & Attach Adapter
        accountAdapter = new AccountAdapter (accounts);
        this.rvAccountList.setAdapter (accountAdapter);

        // Empty message for RecyclerView
        this.tvEmptyMessage = findViewById (R.id.tv_cf_account_empty);
        this.tvEmptyMessage.setVisibility (View.GONE);
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
     * Initialize all the SearchView-related components.
     */
    private void initSearchView () {
        // Filter settings is invisible by default
        this.filterVisible = false;

        // Initialize elements
        this.ibAccountFilterBtn = findViewById (R.id.ib_reminders_filter);
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
                resetRecyclerViewContents ();

                // If there is query input
                if (query.trim ().length () > 0)
                    searchAccounts (query);

                return false;
            }

            // When there is a change in text
            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length () == 0 &&
                    accounts != null &&
                    accountsBackup != null) {
                    resetRecyclerViewContents ();
                }

                return false;
            }
        });
    }

    /**
     * Initialize all the FloatingActionButton-related attributes.
     */
    private void initFABs () {
        // Smaller FABs are hidden by default
        fabShow = false;

        // Retrieve element IDs
        fabAddMain = findViewById (R.id.fab_cashflow_add);
        fabAddEntry = findViewById (R.id.fab_cashflow_add_entry);
        fabAddAccount = findViewById (R.id.fab_cashflow_add_account);
        clAddEntry = findViewById (R.id.cl_add_entry);
        tvAddEntry = findViewById (R.id.tv_fab_add_entry_label);
        clAddAccount = findViewById (R.id.cl_add_account);
        tvAddAccount = findViewById (R.id.tv_fab_add_account_label);

        // OnClickListener for the MAIN FloatingActionButton
        fabAddMain.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (!fabShow) {
                    fabAddMain.requestFocus();
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        // OnClickListener for ADD TRANSACTION FloatingActionButton
        fabAddEntry.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (!accounts.isEmpty ()) {
                    clearFilters ();
                    Intent i = new Intent (CashflowHomeActivity.this, CashflowAddEntryActivity.class);

                    i.putParcelableArrayListExtra (Keys.KEY_CF_ACCS, accounts);

                    startActivity (i);
                    finish ();
                } else {
                    Toast.makeText (CashflowHomeActivity.this, "Add an account first!", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        // OnClickListener for UPDATE TRANSACTION FloatingActionButton
        fabAddAccount.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                clearFilters ();
                Intent i = new Intent (CashflowHomeActivity.this, CashflowAddAccountActivity.class);

                i.putParcelableArrayListExtra (Keys.KEY_CF_ACCS, accounts);

                startActivity (i);
                finish ();
            }
        });
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
        this.clAddAccount.animate ().translationY (0);
        this.clAddEntry.animate ().translationY (0);

    }

    /**
     * Computes and returns the current balance of the user based on all of their accounts.
     *
     * @return the current balance of the user
     */
    private double computeCurrentBalance () {
        double sum = 0;

        for (Account account : accounts)
            sum += account.getBalance ();

        return sum;
    }

    /**
     * Clear the filter. Resets the RecyclerView contents to its default state.
     */
    private void clearFilters () {
        svAccountSearch.setQuery(String.valueOf(""), false);

        // Clear filtered list
        accounts.clear ();
        // Restore original list
        accounts.addAll (accountsBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        accountAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        displayEmptyMessage ();
    }

    // TODO: update searchAccounts method (accountsBackup must have the original data)
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
     * Shows and hides the empty message for the RecyclerView.
     */
    private void displayEmptyMessage () {
        if (accounts.isEmpty ())
            this.tvEmptyMessage.setVisibility (View.VISIBLE);
        else
            this.tvEmptyMessage.setVisibility (View.GONE);
    }

    @Override
    public void onBackPressed() {
        // go back to home activity page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // end cash flow home activity
        finish();
    }
}