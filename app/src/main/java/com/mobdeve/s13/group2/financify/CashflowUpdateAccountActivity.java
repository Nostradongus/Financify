package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.NumberFormat;

/**
 * TODO: Implement & Document CASHFLOW UPDATING OF ACCOUNT
 */
public class CashflowUpdateAccountActivity extends AppCompatActivity {

    EditText etAccountName, etAccountBalance;
    Spinner spType;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashflow_update_account);

        etAccountName = findViewById (R.id.et_cf_update_account_name);
        etAccountBalance = findViewById (R.id.et_cf_update_account_balance);

        Intent i = getIntent ();

        account = new Account (i.getStringExtra (Keys.KEY_ID),
                               i.getStringExtra (Keys.KEY_NAME),
                               i.getFloatExtra (Keys.KEY_BAL, 0),
                               i.getStringExtra (Keys.KEY_TYPE));

        etAccountName.setText (this.account.getName ());
        etAccountBalance.setText (String.valueOf (this.account.getBalance ()));

        this.initAccountTypeSpinner ();
    }

    private void initAccountTypeSpinner () {
        this.spType = findViewById (R.id.sp_cf_account_type);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.account_types, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        spType.setAdapter (spinnerAdapter);

        int pos = -1;
        String type = this.account.getType ();

        if (type.equalsIgnoreCase (Account.TYPE_PHYSICAL))
            pos = 0;
        else if (type.equalsIgnoreCase (Account.TYPE_BANK))
            pos = 1;
        else if (type.equalsIgnoreCase (Account.TYPE_DIGITAL))
            pos = 2;

        spType.setSelection (pos);
    }
}