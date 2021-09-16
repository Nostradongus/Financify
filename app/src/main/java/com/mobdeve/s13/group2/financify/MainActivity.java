package com.mobdeve.s13.group2.financify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

// TODO: add documentation
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * This allows schedule a local notification
         */

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);

        Intent intent = new Intent("singh.ajit.action.DISPLAY_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);

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