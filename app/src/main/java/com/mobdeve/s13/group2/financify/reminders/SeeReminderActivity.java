package com.mobdeve.s13.group2.financify.reminders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.R;

public class SeeReminderActivity extends BaseActivity {

    // get the widgets that change
    private EditText etTitle;
    private EditText etDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reminder);

        // retrieving
        this.etTitle = findViewById(R.id.et_rem_update_entry_title);
        this.etDesc = findViewById(R.id.et_rem_update_entry_desc);

        Intent i = getIntent();

        // TITLE
        String sName = i.getStringExtra(Adapter.KEY_TITLE);
        this.etTitle.setText(sName);

        // DESCRIPTION
        String desc = i.getStringExtra(Adapter.KEY_DESCRIPTION);
        this.etDesc.setText(desc);

    }
}