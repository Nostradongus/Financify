package com.mobdeve.s13.group2.financify.cashflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;

import java.util.ArrayList;

/**
 * This activity serves as the "add an account view" when the user opts to add a Cashflow account.
 */
public class CashflowAddAccountActivity extends BaseActivity {

    // UI Attributes
    private EditText etName, etBal;
    private Spinner spType;
    private Button btnAdd, btnCancel;

    // List of Accounts
    private ArrayList<Account> accounts;

    // Firebase Attributes
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference dbRef;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cash Flow add account layout
        setContentView(R.layout.activity_cashflow_add_account);

        // Initialize general components
        initComponents ();
        // Initialize Firebase components
        initFirebase ();
        // Initialize Spinner components
        initAccountTypeSpinner ();
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
        Intent i = new Intent (CashflowAddAccountActivity.this, CashflowHomeActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (CashflowAddAccountActivity.this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Adds the newly created Account to the Firebase database
     *
     * @param name      the name of the new Account
     * @param balance   the balance of the new Account
     * @param type      the type of the new Account
     */
    private void addAccountInFirebase (String name, double balance, String type) {
        // New Account ID
        String newId;

        // If there are accounts, generate latest ID
        if (!accounts.isEmpty ())
            newId = String.valueOf (Integer.parseInt (accounts.get (accounts.size () - 1).getId ()) + 1);
        // If no accounts yes
        else
            newId = "0";

        // New DB reference to the new Account
        DatabaseReference newRef = dbRef.child (newId);

        // Set Account values in DB
        newRef.child ("balance").setValue (balance);
        newRef.child ("name").setValue (name);
        newRef.child ("type").setValue (type);
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
            // Create DB reference to this user's Cashflow accounts
            dbRef = FirebaseDatabase.getInstance ().getReference ()
                    .child ("users")
                    .child (userId)
                    .child ("accounts");
            // If invalid session
        }
        else {
            goBackToLogin ();
        }
    }

    /**
     * Initialize all the general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        btnAdd = findViewById (R.id.btn_cf_add_account);
        etName = findViewById (R.id.et_cf_account_name);
        etBal = findViewById (R.id.et_cf_account_balance);
        btnCancel = findViewById (R.id.btn_cf_cancel_add_account);

        // Cancel Button OnClickListener
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToHomepage ();
            }
        });

        // Retrieve accounts passed from Intent (using Parcelable)
        Intent i = getIntent ();
        // Instantiate accounts list
        accounts = new ArrayList<> (i.getParcelableArrayListExtra (Keys.KEY_CF_ACCS));

        // Add Button OnClickListener
        btnAdd.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // If all fields are valid
                if (isValidForm ()) {
                    // Retrieve form data
                    String name = etName.getText ().toString ();
                    double balance = Double.parseDouble (etBal.getText ().toString ());
                    String type = spType.getSelectedItem ().toString ();

                    // Add to Firebase
                    addAccountInFirebase (name, balance, type);

                    // End activity
                    goBackToHomepage ();
                // If there are invalid fields
                } else {
                    Toast.makeText (CashflowAddAccountActivity.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
                }
            }
        });
    }

    /**
     * Initialize Spinner components.
     */
    private void initAccountTypeSpinner () {
        // Retrieve element ID
        this.spType = findViewById (R.id.sp_cf_account_type);

        // Retrieve & Attach Spinner contents
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.account_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter (spinnerAdapter);
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        boolean isValid = true;

        // If Account Name is improperly filled
        if (etName.getText ().toString ().trim ().isEmpty ()) {
            etName.setError ("Please input an account name!");
            isValid = false;
        }

        if (isExistingAccountName (etName.getText ().toString ().trim ())) {
            etName.setError ("Account name already taken!");
            isValid = false;
        }

        // If Account Balance is improperly filled
        if (etBal.getText ().toString ().trim ().isEmpty ()) {
            etBal.setError ("Please input a balance!");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks if a given Account name already exists.
     *
     * @param name  the Account name to be checked
     * @return  true if existing name, otherwise false
     */
    private boolean isExistingAccountName (String name) {
        for (Account account : accounts)
            if (account.getName ().equalsIgnoreCase (name))
                return true;

        return false;
    }
}