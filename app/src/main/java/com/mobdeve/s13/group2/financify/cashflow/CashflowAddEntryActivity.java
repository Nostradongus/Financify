package com.mobdeve.s13.group2.financify.cashflow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;
import com.mobdeve.s13.group2.financify.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This activity serves as the "add a transaction view" when the user opts to add a Cashflow entry.
 */
public class CashflowAddEntryActivity extends BaseActivity {

    // UI Attributes
    private EditText etAmt, etDesc;
    private DatePickerDialog datePickerDialog;
    private Button btnDate, btnAdd, btnCancel;
    private Spinner spTransType;
    private AutoCompleteTextView actvTransAccount;

    // List of Accounts
    private ArrayList<Account> accounts;

    // Firebase Attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Cash Flow add transaction layout
        setContentView (R.layout.activity_cashflow_add_entry);

        // Initialize DatePicker components
        initDatePicker ();
        // Initialize general components
        initComponents ();
        // Initialize Spinner components
        initTransSpinners ();
        // Initialize Firebase components
        initFirebase ();
    }

    /**
     * When the user presses the back button of the device.
     */
    @Override
    public void onBackPressed () {
        super.onBackPressed ();
        goBackToHomepage ();
    }

    /**
     * Launches an activity leading to the Cashflow Homepage and finishes this activity.
     */
    private void goBackToHomepage () {
        Intent i = new Intent (CashflowAddEntryActivity.this, CashflowHomeActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (CashflowAddEntryActivity.this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Adds the newly created Transaction to the Firebase database.
     *
     * @param accountIndex  the list index of the Account to be added
     */
    private void addEntryInFirebase (int accountIndex) {
        // Account (parent) ID of Transaction
        String accountId = accounts.get (accountIndex).getId ();
        // Retrieve newly created Transaction
        Transaction transaction = accounts.get (accountIndex).getLatestTransaction ();
        // Retrieve ID of newly created Transaction
        String latestTransactionId = transaction.getId ();

        // Create new reference to new Transaction DB directory
        DatabaseReference newRef = dbRef.child (accountId).child ("transactions").child (latestTransactionId);

        // Update balance of Account (parent) in DB
        dbRef.child (accountId).child ("balance").setValue (accounts.get (accountIndex).getBalance ());

        // Insert new Transaction
        newRef.child ("accountId").setValue (transaction.getAccountId ());
        newRef.child ("amount").setValue (transaction.getAmount ());
        newRef.child ("date").setValue (transaction.getDate ());
        newRef.child ("description").setValue (transaction.getDescription ());
        newRef.child ("type").setValue (transaction.getType ());
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

            dbRef = FirebaseDatabase.getInstance ().getReference ("users")
                    .child (userId).child ("accounts");
        // If invalid session
        } else {
            // TODO: Verify if redirect to login is working
            goBackToLogin ();
        }
    }

    /**
     * Initialize all the general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        etAmt = findViewById (R.id.et_cf_entry_amount);
        etDesc = findViewById (R.id.et_cf_entry_desc);
        btnCancel = findViewById (R.id.btn_cf_cancel_add_entry);
        btnDate = findViewById (R.id.btn_cf_entry_date);
        btnAdd = findViewById (R.id.btn_cf_add_entry);

        // Retrieve accounts passed from Intent (using Parcelable)
        Intent i = getIntent ();
        // Instantiate accounts list
        accounts = new ArrayList<> (i.getParcelableArrayListExtra (Keys.KEY_CF_ACCS));

        // Cancel Button OnClickListener
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToHomepage ();
            }
        });

        // Set default date
        btnDate.setText (DateHelper.getDateToday ());

        // Date Button OnClickListener
        btnDate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                openDatePicker ();
            };
        });

        // Add Button OnClickListener
        btnAdd.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // Clean Account selection first
                checkSelectedAccount ();

                // If all fields are valid
                if (isValidForm ()) {
                    // Retrieve index of selected Account for this new Transaction
                    int accountIndex = getAccountIndex (actvTransAccount.getText ().toString ());

                    // Add a Transaction to this Account
                    accounts.get (accountIndex).addTransaction (
                            etDesc.getText ().toString (),
                            Float.parseFloat (etAmt.getText ().toString ()),
                            spTransType.getSelectedItem ().toString (),
                            btnDate.getText ().toString ()
                    );

                    // Update Firebase data
                    addEntryInFirebase (accountIndex);

                    // End activity
                    goBackToHomepage ();
                // If there are invalid fields
                } else {
                    Toast.makeText (CashflowAddEntryActivity.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
                }
            }
        });
    }

    /**
     * Initialize Spinner components.
     */
    private void initTransSpinners () {
        /* "Type" SPINNER */
        // Retrieve "Type" Spinner ID
        spTransType = findViewById (R.id.sp_cf_entry_type);

        // Set "Type" Spinner content using resource file
        ArrayAdapter<CharSequence> spTypeAdapter = ArrayAdapter.createFromResource (
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );

        // Set "Type" Spinner View Resource & Adapter
        spTypeAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spTransType.setAdapter (spTypeAdapter);



        /* "Account" AutoCompleteTextView */
        // Retrieve "Account" ACTV ID
        actvTransAccount = findViewById (R.id.actv_cf_entry_account);

        // Create an ArrayList of Account names
        ArrayList<CharSequence> accountNames = new ArrayList<> ();
        for (Account account : accounts)
            accountNames.add (account.getName ());

        // Attach ArrayList to Adapter
        ArrayAdapter<CharSequence> atcvAccountsAdapter = new ArrayAdapter<CharSequence> (this, android.R.layout.simple_list_item_1, accountNames);
        // Set ACTV Adapter
        actvTransAccount.setAdapter (atcvAccountsAdapter);

        // ACTV OnFocusChangeListener
        actvTransAccount.setOnFocusChangeListener (new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange (View v, boolean hasFocus) {
                // If ACTV loses focus
                if (!hasFocus) {
                    checkSelectedAccount ();
                } else {
                    actvTransAccount.showDropDown ();
                }
            }
        });

        // ACTV OnClickListener (for drop down functionality_
        actvTransAccount.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                actvTransAccount.showDropDown ();
            }
        });

        // Immediately show suggestions on focus
        actvTransAccount.setThreshold (0);
    }

    /**
     * Initialize DatePicker components.
     */
    private void initDatePicker () {
        // OnDateSetListener for DatePickerDialog
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // TODO: Try to implement this version of dates
                // String date = DateHelper.makeDateString (day, month, year);
                String date = month + "/" + day + "/" + year;
                btnDate.setText (date);
            }
        };

        // Get date today
        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);

        // Set default date for DatePickerDialog
        datePickerDialog = new DatePickerDialog (this, R.style.datepicker, dateSetListener, year, month, day);
    }

    /**
     * Shows the DatePickerDialog for this activity.
     */
    public void openDatePicker() {
        datePickerDialog.show();
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        boolean isValid = true;

        // If amount is improperly filled
        if (etAmt.getText ().toString ().trim ().isEmpty ()) {
            etAmt.setError ("Please input an amount!");
            isValid = false;
        }

        // If an invalid Transaction type is chosen
        if (spTransType.getSelectedItem ().toString ().equalsIgnoreCase ("Select type…")) {
            TextView errorText = (TextView) spTransType.getSelectedView ();
            errorText.setTextColor (Color.RED);
            errorText.setText (new String ("Select type…"));
            isValid = false;
        }

        // If Account Dropdown is improperly filled
        if (actvTransAccount.getText ().toString ().trim ().isEmpty ()) {
            actvTransAccount.setError ("Account does not exist!");
            isValid = false;
        }

        // If description is improperly filled
        if (etDesc.getText ().toString ().trim ().isEmpty ()) {
            etDesc.setError ("Please input a description!");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Retrieves the index of an Account given its name.
     *
     * @param accName   the name of the Account being searched for
     * @return  the index of the Account found, if it exists
     */
    private int getAccountIndex (String accName) {
        // Loop through each Account in the list
        for (int i = 0; i < accounts.size (); i++)
            // If matching, return index
            if (accounts.get (i).getName ().toLowerCase ().equalsIgnoreCase (accName))
                return i;

        // Invalid name
        return -1;
    }

    /**
     * Validation. Check if selected Account belongs to the list.
     */
    private void checkSelectedAccount () {
        String str = actvTransAccount.getText().toString();
        ListAdapter listAdapter = actvTransAccount.getAdapter();

        // Check if input is valid
        for (int i = 0; i < listAdapter.getCount(); i++) {
            String temp = listAdapter.getItem (i).toString ();
            // If strings are equal
            if (str.compareTo (temp) == 0) {
                return;
            }
        }

        // If string is not in list, empty field
        actvTransAccount.setText("");
    }
}
