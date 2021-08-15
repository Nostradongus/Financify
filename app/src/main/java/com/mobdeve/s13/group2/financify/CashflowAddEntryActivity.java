package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * TODO: Implement & Document CASHFLOW ADDING OF TRANSACTION
 */
public class CashflowAddEntryActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button btnDate;
    private Spinner spTransType, spAccounts;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cashflow_add_entry);

        initDatePicker ();

        btnDate = findViewById (R.id.btn_cf_entry_date);
        btnDate.setText (getDateToday ());
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
//        this.spAccounts = findViewById (R.id.sp_cf_account);
//        ArrayAdapter<CharSequence> spAccountsAdapter = ArrayAdapter.createFromResource (
//                this, /* ARRAY OF ACCOUNTS OF USER */, android.R.layout.simple_spinner_item
//        );
//
//        spAccountsAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
//
//        spTransType.setAdapter (spAccountsAdapter);
    }

    private String getDateToday () {
        Calendar calendar = Calendar.getInstance ();
        int year = calendar.get (Calendar.YEAR);
        int month = calendar.get (Calendar.MONTH);
        month = month + 1;
        int day = calendar.get (Calendar.DAY_OF_MONTH);
        return makeDateString (day, month, year);
    }

    private void initDatePicker () {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString (day, month, year);
                btnDate.setText (date);
            }
        };

        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog (this, R.style.datepicker, dateSetListener, year, month, day);

    }

    private String makeDateString (int day, int month, int year) {
        return getMonthFormat (month) + " " + day + " " + year;
    }

    private String getMonthFormat (int month) {
        String[] months = new String[] {
                "JAN", "FEB", "MAR", "APR",
                "MAY", "JUN", "JUL", "AUG",
                "SEP", "OCT", "NOV", "DEC"
        };

        if (month > 0 && month < 12)
            return months[month - 1];

        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}