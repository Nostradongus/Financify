package com.mobdeve.s13.group2.financify.reminders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mobdeve.s13.group2.financify.R;

public class SeeReminderActivity extends AppCompatActivity {

    // get the widgets that change
    private TextView tvTitle;
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reminder);

        // retrieving
        this.tvTitle = findViewById(R.id.tv_reminders_title);
        this.tvDesc = findViewById(R.id.tv_reminders_description);

        Intent i = getIntent();

        // TITLE
        String sName = i.getStringExtra(Adapter.KEY_TITLE);
        this.tvTitle.setText(sName);

        // DESCRIPTION
        String desc = i.getStringExtra(Adapter.KEY_DESCRIPTION);
        this.tvDesc.setText(desc);

    }
}