package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * For splash screen activity / page, to show splash screen before moving to the application's
 * login or home page.
 */
public class MainActivity extends AppCompatActivity {

    // specified duration period for application's duration screen
    private static final int SPLASH_SCREEN_DELAY = 850;

    // indicator when FirebaseDatabase persistence is already enabled
    private static boolean isPersistenceEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enable persistence for storing data in local cache for offline use
        if (!isPersistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }

        // splash (intro) screen for 3 seconds then redirect to login activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // setup intent to redirect based on user session
                Intent intent;

                // if there is a user currently logged in the application
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // redirect to application's home activity page
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                } else {
                    // redirect to login activity page for user to log in the application
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }

                // redirect to appropriate activity page
                startActivity(intent);

                // end splash screen activity
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
