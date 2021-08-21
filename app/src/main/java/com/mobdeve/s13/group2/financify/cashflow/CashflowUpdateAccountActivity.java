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
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;

import java.util.Arrays;
import java.util.List;

/**
 * This activity serves as the "update an account view" when the user opts to edit a Cashflow account.
 */
public class CashflowUpdateAccountActivity extends BaseActivity {

    // UI Attributes
    private Button btnUpdate, btnDelete, btnCancel;
    private EditText etAccountName;
    private Spinner spType;

    // Account to be edited
    private Account account;

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
        // Cash Flow update account layout
        setContentView(R.layout.activity_cashflow_update_account);

        // Initialize general components
        initComponents ();
        // Initialize Spinner components
        initAccountTypeSpinner ();
        // Initialize Firebase components
        initFirebase ();
    }

    /**
     * When the user presses the back button of the device.
     */
    @Override
    public void onBackPressed () {
        super.onBackPressed ();

        // Go back to homepage
        goBackToHomepage ();
    }

    /**
     * Launches an activity leading to the Cashflow Account page and finishes this activity.
     */
    private void goBackToAccountPage () {
        Intent i = new Intent (CashflowUpdateAccountActivity.this, CashflowAccountActivity.class);
        i.putExtra (Keys.KEY_ACC, account);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Cashflow Homepage and finishes this activity.
     */
    private void goBackToHomepage () {
        Intent i = new Intent (CashflowUpdateAccountActivity.this, CashflowHomeActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Updates the Account in the Firebase database.
     *
     * @param name  the new name of the Account
     * @param type  the new type of the Account
     */
    private void updateAccountInFirebase (String name, String type) {
        // Create new reference
        DatabaseReference newRef = dbRef.child (account.getId ());

        // Set DB values
        newRef.child ("name").setValue (name);
        newRef.child ("type").setValue (type);
    }

    /**
     * Deletes the current Account in the Firebase database.
     */
    private void deleteAccountInFirebase () {
        // Remove child
        dbRef.child (account.getId ()).removeValue ();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ();

        // If valid session
        if (user != null) {
            userId = user.getUid ();
            dbRef = FirebaseDatabase.getInstance ().getReference ()
                    .child ("users")
                    .child (userId)
                    .child ("accounts");
        // If invalid session
        } else {
            // TODO: Handle when session is invalid.
        }
    }

    /**
     * Initialize general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        etAccountName = findViewById (R.id.et_cf_update_account_name);
        btnUpdate = findViewById (R.id.btn_cf_update_account);
        btnDelete = findViewById (R.id.btn_cf_delete_account);
        btnCancel = findViewById (R.id.btn_cf_cancel_update_account);

        // Cancel Button OnClickListener
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToAccountPage ();
            }
        });

        // Update Button OnClickListener
        btnUpdate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // If valid form
                if (isValidForm ()) {
                    // Retrieve fields
                    String name = etAccountName.getText ().toString ();
                    String type = spType.getSelectedItem ().toString ();

                    // Update in Firebase
                    updateAccountInFirebase (name, type);

                    // Update local Account
                    account.setName (name);
                    account.setType (type);

                    // Exit activity
                    goBackToAccountPage ();
                    // If invalid form
                } else {
                    Toast.makeText (CashflowUpdateAccountActivity.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        // Delete Button OnClickListener
        btnDelete.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // Delete Account in Firebase
                deleteAccountInFirebase ();

                // Exit activity
                goBackToHomepage ();
            }
        });

        // Retrieve Account from Account page
        Intent i = getIntent ();
        account = i.getParcelableExtra (Keys.KEY_ACC);

        // Set field value
        etAccountName.setText (this.account.getName ());
    }

    /**
     * Initialize Spinner components.
     */
    private void initAccountTypeSpinner () {
        // Retrieve element ID
        spType = findViewById (R.id.sp_cf_account_type);

        // Retrieve & Attach Spinner contents
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.account_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter (spinnerAdapter);

        // Set default value based on Account's type
        int pos = 0;
        String accountType = account.getType ();

        List<String> accountTypes = Arrays.asList (getResources().getStringArray(R.array.account_types));
        for (int i = 0; i < accountTypes.size (); i++)
            if (accountTypes.get (i).equalsIgnoreCase (accountType)) {
                pos = i;
                break;
            }

        spType.setSelection (pos);
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        // If Account name is improperly filled
        if (etAccountName.getText ().toString ().trim ().isEmpty ()) {
            etAccountName.setError ("Please input an account name!");
            return false;
        }

        return true;
    }
}