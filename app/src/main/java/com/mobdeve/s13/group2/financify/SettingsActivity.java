package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// TODO: add documentation
public class SettingsActivity extends BaseActivity {

    // home button layout
    private ConstraintLayout clHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize components
        initComponents();
    }

    private void initComponents() {
        this.clHomeBtn = findViewById(R.id.cl_settings_back_home_nav);

        this.clHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToHomePage();
            }
        });
    }

    private void goBackToHomePage() {
        // redirect back to home activity page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // finish current activity
        finish();
    }

    @Override
    public void onBackPressed() {
        goBackToHomePage();
    }
}