package com.mobdeve.s13.group2.financify.cashflow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;
import com.mobdeve.s13.group2.financify.model.Transaction;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * This activity serves as the "update a transaction view" when the user opts to edit a Cashflow entry.
 */
public class CashflowUpdateEntryActivity extends BaseActivity {

    // UI Attributes
    private EditText etAmt, etDesc;
    private Button btnDate, btnUpdate, btnDelete, btnCancel;
    private DatePickerDialog datePickerDialog;
    private Spinner spTransType;

    // Transaction to be updated
    private Transaction transaction;
    // Account affiliated with Transaction
    private Account account;

    // Firebase Attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;

    /**
     * When the activity is created, this function is also run once.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cash Flow update transaction layout
        setContentView(R.layout.activity_cashflow_update_entry);

        // Initialize general components
        initComponents ();
        // Initialize DatePicker components
        initDatePicker ();
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
        goBackToAccountPage ();
    }

    /**
     * Launches an activity leading to the Cashflow Account page and finishes this activity.
     */
    private void goBackToAccountPage () {
        Intent i = new Intent (CashflowUpdateEntryActivity.this, CashflowAccountActivity.class);
        i.putExtra (Keys.KEY_CF_ACC, account);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (CashflowUpdateEntryActivity.this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Updates the Transaction in the Firebase database.
     */
    private void updateEntryInFirebase () {
        // Update balance in DB
        FirebaseDatabase.getInstance ().getReference ("users")
                .child (userId)
                .child ("accounts")
                .child (transaction.getAccountId ())
                .child ("balance")
                .setValue (account.getBalance ());

        // Set DB values
        dbRef.child ("amount").setValue (transaction.getAmount ());
        dbRef.child ("date").setValue (transaction.getDate ());
        dbRef.child ("description").setValue (transaction.getDescription ());
        dbRef.child ("type").setValue (transaction.getType ());
    }

    /**
     * Deletes the current Transaction in the Firebase database.
     */
    private void deleteEntryInFirebase () {
        // Update balance in DB
        FirebaseDatabase.getInstance ().getReference ("users")
                .child (userId)
                .child ("accounts")
                .child (transaction.getAccountId ())
                .child ("balance")
                .setValue (account.getBalance ());

        // Remove child
        dbRef.removeValue ();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        // Instantiate lists
        Intent i = getIntent ();
        String parentAccountId = transaction.getAccountId ();
        this.mAuth = FirebaseAuth.getInstance ();
        this.user = this.mAuth.getCurrentUser ();

        // If valid session
        if (user != null) {
            this.userId = user.getUid ();
            this.dbRef = FirebaseDatabase.getInstance ().getReference ("users")
                    .child (userId)
                    .child ("accounts")
                    .child (parentAccountId)
                    .child ("transactions")
                    .child (transaction.getId ());
        // If invalid session
        } else {
            // TODO: Verify if redirect to login is working
            goBackToLogin ();
        }
    }

    /**
     * Initialize general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        btnUpdate = findViewById (R.id.btn_cf_update_entry);
        btnDelete = findViewById (R.id.btn_cf_delete_entry);
        btnCancel = findViewById (R.id.btn_cf_cancel_update_entry);
        etAmt = findViewById (R.id.et_cf_update_entry_amount);
        etDesc = findViewById (R.id.et_cf_update_entry_desc);
        btnDate = findViewById (R.id.btn_cf_update_entry_date);

        // Cancel Button OnClickListener
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToAccountPage ();
            }
        });

        // Update Button OnClickListener
        btnUpdate.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // If valid form
                if (isValidForm ()) {
                    // Retrieve fields
                    String desc = etDesc.getText ().toString ();
                    double amt = Double.parseDouble (etAmt.getText ().toString ());
                    String type = spTransType.getSelectedItem ().toString ();
                    String date = btnDate.getText ().toString ();

                    // Update local Transaction
                    transaction.setDescription (desc);
                    transaction.setAmount (amt);
                    transaction.setType (type);
                    transaction.setDate (date);

                    // Update account balance
                    account.updateTransaction (
                            transaction.getId (),
                            desc,
                            amt,
                            type,
                            date
                    );

                    // Update Transaction and Account in Firebase
                    updateEntryInFirebase ();

                    // Exit activity
                    goBackToAccountPage ();
                // If invalid form
                } else {
                    Toast.makeText (CashflowUpdateEntryActivity.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        // Delete Button OnClickListener
        btnDelete.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                boolean success = account.removeTransaction (transaction.getId ());

                if (success) {
                    deleteEntryInFirebase ();
                    goBackToAccountPage ();
                } else {
                    Toast.makeText (CashflowUpdateEntryActivity.this, "Unable to delete entry!", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        // Retrieve Account from Account page
        Intent i = getIntent ();
        account = i.getParcelableExtra (Keys.KEY_CF_ACC);
        transaction = i.getParcelableExtra (Keys.KEY_CF_TRAN);

        // Set field values
        etAmt.setText (String.valueOf (transaction.getAmount()));
        etDesc.setText (transaction.getDescription ());
        btnDate.setText (transaction.getDate ());

        // Date Button OnClickListener
        btnDate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                openDatePicker ();
            };
        });
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
     * Initialize Spinner components.
     */
    private void initTransSpinners () {
        // Retrieve "Type" Spinner ID
        spTransType = findViewById (R.id.sp_cf_update_entry_type);

        // Set "Type" Spinner content using resource file
        ArrayAdapter<CharSequence> spTypeAdapter = ArrayAdapter.createFromResource (
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );

        // Set "Type" Spinner View Resource & Adapter
        spTypeAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spTransType.setAdapter (spTypeAdapter);

        // Set Spinner selection to Transaction type
        List<String> transTypes = Arrays.asList(getResources().getStringArray(R.array.transaction_types));
        spTransType.setSelection (transTypes.indexOf (transaction.getType ()));
    }

    /**
     * Shows the DatePickerDialog for this activity.
     */
    private void openDatePicker () {
        datePickerDialog.show ();
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

        // If description is improperly filled
        if (etDesc.getText ().toString ().trim ().isEmpty ()) {
            etDesc.setError ("Please input a description!");
            isValid = false;
        }

        return isValid;
    }
}