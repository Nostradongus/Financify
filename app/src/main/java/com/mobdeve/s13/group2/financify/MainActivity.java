package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * For splash screen activity / page, to show splash screen before moving to the application's
 * login or home page.
 */
public class MainActivity extends AppCompatActivity {

    // specified duration period for application's duration screen
    private static final int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enable persistence for storing data in local cache for offline use
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // splash (intro) screen for 3 seconds then redirect to login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // if there is a user logged in the application
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // redirect to application's home page
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);

                    // end splash screen activity
                    finish();
                } else {
                    // redirect to login page for user to log in the application
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    // end splash screen activity
                    finish();
                }
            }
        }, SPLASH_SCREEN_DELAY);
    }
}