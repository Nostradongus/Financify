package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // splash (intro) screen for 3 seconds then redirect to login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: to be changed, checking for when user is logged in or not
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                // finish splash screen afterwards
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}