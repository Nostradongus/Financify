package com.mobdeve.s13.group2.financify.cashflow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.mobdeve.s13.group2.financify.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * TODO: Implement & Document CASHFLOW ADDING OF TRANSACTION
 */
public class CashflowAddEntryActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button btnDate;
    private Spinner spTransType;
    private AutoCompleteTextView atcvTransAccount;

    private ArrayList<Account> accounts;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cashflow_add_entry);

        initDatePicker ();

        btnDate = findViewById (R.id.btn_cf_entry_date);
        btnDate.setText (DateHelper.getDateToday ());
        btnDate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                openDatePicker (view);
            };
        });

        this.initTransSpinners ();
    }

    private void initTransSpinners () {
        this.spTransType = findViewById (R.id.sp_cf_entry_type);
        ArrayAdapter<CharSequence> spTypeAdapter = ArrayAdapter.createFromResource (
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );

        spTypeAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        spTransType.setAdapter (spTypeAdapter);

        /*
            TODO: Implement account selection spinner when DATABASE IS IMPLEMENTED.
            This requires access to ALL of the user's accounts.
         */

        this.atcvTransAccount = findViewById (R.id.actv_cf_entry_account);

        ArrayAdapter<String> atcvAccountsAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1,  generateAccounts());
        atcvTransAccount.setAdapter (atcvAccountsAdapter);

    }

    private void initDatePicker () {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = DateHelper.makeDateString (day, month, year);
                btnDate.setText (date);
            }
        };

        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog (this, R.style.datepicker, dateSetListener, year, month, day);

    }


    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    // TODO: TEMPORARY; TO BE REMOVED
    private ArrayList<String> generateAccounts () {
//        accounts = new ArrayList<> ();
//
//        accounts.add (new Account ("0","BDO", 500, Account.TYPE_BANK));
//        accounts.add (new Account ("1","BPI", 500, Account.TYPE_BANK));
//        accounts.add (new Account ("2","GCash", 500, Account.TYPE_DIGITAL));

        ArrayList<String> test = new ArrayList<> ();
        test.add ("BDO");
        test.add ("BPI");
        test.add ("GCash");

        return test;
    }
}