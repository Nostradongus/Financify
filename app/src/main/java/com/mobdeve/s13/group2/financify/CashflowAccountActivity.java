package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

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
    private TextView tvAccountName, tvAccountType, tvBalance;
    // ImageView to be utilized as a Button
    private ImageView ivEditBtn;

    private Account account;
    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cash Flow specific account layout
        setContentView(R.layout.activity_cashflow_account);

        // Instantiate attributes
        this.transactions = new ArrayList<> ();

        this.tvAccountName = findViewById (R.id.tv_specific_account_name);
        this.tvAccountType = findViewById (R.id.tv_specific_account_type);
        this.tvBalance = findViewById (R.id.tv_specific_account_balance);
        this.ivEditBtn = findViewById (R.id.iv_cashflow_edit_account);

        // Retrieve essential information passed from the homepage activity
        Intent i = getIntent ();

        account = new Account (i.getStringExtra (Keys.KEY_ID),
                               i.getStringExtra (Keys.KEY_NAME),
                               i.getFloatExtra (Keys.KEY_BAL, 0),
                               i.getStringExtra (Keys.KEY_TYPE));

        // TODO: Implement when CASHFLOW ACCOUNT UPDATING is done.
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

        // Initialize RecyclerView components
        this.initRecyclerView ();
    }

    /**
     * Initializes all the RecyclerView components.
     */
    private void initRecyclerView () {
        // Initialize RecyclerView for transactions
        this.rvTransList = findViewById (R.id.rv_transactions);

        // Instantiate & Attach LayoutManager
        this.transManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvTransList.setLayoutManager (transManager);

        // TODO: replace with db codes
        this.initData ();

        // Instantiate & Attach Adapter
        transAdapter = new TransactionAdapter (transactions);
        this.rvTransList.setAdapter (transAdapter);
    }

    // TODO: temporary, delete when db is implemented
    private void initData () {
        this.transactions.add (new Transaction ("Paid tuition fee", 63000, Transaction.TYPE_EXPENSE, "01/01/2021"));
        this.transactions.add (new Transaction ("Monthly income", 100000, Transaction.TYPE_INCOME, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));

        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
        this.transactions.add (new Transaction ("Invested in Crypto", 20000, Transaction.TYPE_INVESTMENT, "01/01/2021"));
    }
}