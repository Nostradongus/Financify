package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This activity serves as the "landing page" when a user navigates to the Cash Flow section of
 * the mobile application.
 */
public class CashflowHomeActivity extends AppCompatActivity {

    // RecyclerView attributes
    private RecyclerView rvAccountList;
    private LinearLayoutManager accountManager;
    private AccountAdapter accountAdapter;
    private ArrayList<Account> accounts;

    // FloatingActionButtons for various add functionalities
    private FloatingActionButton fabAddMain, fabAddEntry, fabAddAccount;
    // Boolean that represents if the smaller FloatingActionButtons will be shown.
    private boolean fabShow;
    // Elements for the FloatingActionButton implementation
    private ConstraintLayout clAddEntry, clAddAccount;
    private TextView tvAddEntry, tvAddAccount;

    // TextView for Current Balance
    private TextView tvCurrBal;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow homepage layout
        setContentView (R.layout.activity_cashflow_home);

        // Instantiate attributes
        this.fabShow = false;
        this.accounts = new ArrayList<> ();
        this.fabAddMain = findViewById (R.id.fab_cashflow_add);
        this.fabAddEntry = findViewById (R.id.fab_cashflow_add_entry);
        this.fabAddAccount = findViewById (R.id.fab_cashflow_add_account);

        this.clAddEntry = findViewById (R.id.cl_add_entry);
        this.tvAddEntry = findViewById (R.id.tv_fab_add_entry_label);
        this.clAddAccount = findViewById (R.id.cl_add_account);
        this.tvAddAccount = findViewById (R.id.tv_fab_add_account_label);

        this.tvCurrBal = findViewById (R.id.tv_cashflow_current_balance);

        // OnClickListener for the main FloatingActionButton
        this.fabAddMain.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (!fabShow)
                    showFABMenu ();
                else
                    closeFABMenu ();
            }
        });

        // TODO: Implement when CASHFLOW TRANSACTION ADDING is done.
        this.fabAddEntry.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowHomeActivity.this, CashflowAddEntryActivity.class);
                startActivity (i);
            }
        });

        // TODO: Implement when CASHFLOW ACCOUNT ADDING is done.
        this.fabAddAccount.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (CashflowHomeActivity.this, CashflowAddAccountActivity.class);
                startActivity (i);
            }
        });

        // Initialize RecyclerView components
        this.initRecyclerView ();

        // Set Current Balance TextView value
        this.tvCurrBal.setText (NumberFormat.getCurrencyInstance().format(this.computeCurrentBalance ()));
    }

    /**
     * Initializes all the RecyclerView components.
     */
    private void initRecyclerView () {
        // Initialize RecyclerView for accounts
        this.rvAccountList = findViewById (R.id.rv_accounts);

        // Instantiate & Attach LayoutManager
        this.accountManager = new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false);
        this.rvAccountList.setLayoutManager (accountManager);

        // TODO: replace with db codes
        this.initData ();

        // Instantiate & Attach Adapter
        accountAdapter = new AccountAdapter (accounts);
        this.rvAccountList.setAdapter (accountAdapter);
    }

    // TODO: temporary, delete when db is implemented
    private void initData () {
//        this.accounts.add (new Account ("1", "My Physical Wallet", 1000, Account.TYPE_PHYSICAL));
//        this.accounts.add (new Account ("2", "My BDO Account", 30000, Account.TYPE_BANK));
//        this.accounts.add (new Account ("3", "My Shopee Wallet", 5040, Account.TYPE_DIGITAL));
//        this.accounts.add (new Account ("4", "My GCash", 5040, Account.TYPE_DIGITAL));
//        this.accounts.add (new Account ("5", "My EastWest Account", 30000, Account.TYPE_BANK));

        this.accounts.add (new Account ("1", "My Physical Wallet", 999999999, Account.TYPE_PHYSICAL));
        this.accounts.add (new Account ("2", "My BDO Account", 999999999, Account.TYPE_BANK));
        this.accounts.add (new Account ("3", "My Shopee Wallet", 999999999, Account.TYPE_DIGITAL));
        this.accounts.add (new Account ("4", "My GCash", 999999999, Account.TYPE_DIGITAL));
        this.accounts.add (new Account ("5", "My EastWest Account", 999999999, Account.TYPE_BANK));
    }

    /**
     * Displays the smaller FloatingActionButtons when the main FAB button is pressed.
     */
    private void showFABMenu (){
        this.fabShow = true;
        this.clAddAccount.animate().translationY(-getResources().getDimension (R.dimen.standard_55));
        this.clAddEntry.animate().translationY(-getResources().getDimension (R.dimen.standard_105));

        this.tvAddEntry.setVisibility (View.VISIBLE);
        this.tvAddAccount.setVisibility (View.VISIBLE);
    }

    /**
     * Hides the smaller FloatingActionButtons when the main FAB button is pressed.
     */
    private void closeFABMenu (){
        this.fabShow = false;
        this.tvAddEntry.setVisibility (View.GONE);
        this.tvAddAccount.setVisibility (View.GONE);

        this.clAddAccount.animate().translationY(0);
        this.clAddEntry.animate().translationY(0);
    }

    /**
     * Computes and returns the current balance of the user.
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
}