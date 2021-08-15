package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * TODO: Implement & Document CASHFLOW ADDING OF ACCOUNT
 */
public class CashflowAddAccountActivity extends AppCompatActivity {

    private Spinner spType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashflow_add_account);

        this.initAccountTypeSpinner ();
    }

    private void initAccountTypeSpinner () {
        this.spType = findViewById (R.id.sp_cf_account_type);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.account_types, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        spType.setAdapter (spinnerAdapter);
    }
}