package com.mobdeve.s13.group2.financify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.helpers.Keys;
import com.mobdeve.s13.group2.financify.model.Model;
import com.mobdeve.s13.group2.financify.pin.PinActivity;
import com.mobdeve.s13.group2.financify.pin.RegisterPINActivity;

import org.jetbrains.annotations.NotNull;

/**
 * For splash screen activity / page, to show splash screen before moving to the application's
 * login or home page.
 */
public class MainActivity extends AppCompatActivity {

    // specified duration period for application's duration screen
    private static final int SPLASH_SCREEN_DELAY = 850;

    // indicator when FirebaseDatabase persistence is already enabled
    private static boolean isPersistenceEnabled = false;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize Firebase components
        initFirebase ();

        // splash (intro) screen for 3 seconds then redirect to login activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                // if there is a user currently logged in the application
                if (mAuth.getCurrentUser() != null) {
                    // redirect to application's home activity page
//                    Intent intent;
//                    intent = new Intent(MainActivity.this, HomeActivity.class);
//
//                    // redirect to appropriate activity page
//                    startActivity(intent);
//
//                    // end splash screen activity
//                    finish();


                    // retrieve user PIN from Firebase
                    database.getReference (Model.users.name ())
                            .child (mAuth.getCurrentUser ().getUid ())
                            .child (Model.pin.name ())
                            .get ()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // retrieve PIN object
                                        Object userPIN = task.getResult ().getValue ();

                                        // setup intent to redirect based on if user already has PIN or not
                                        Intent intent;

                                        // if user has no PIN just yet
                                        if (userPIN == null) {
                                            // redirect to PIN creation page
                                            intent = new Intent (MainActivity.this, RegisterPINActivity.class);
                                            intent.putExtra (Keys.KEY_PIN_NEW, false);
                                        // if user has PIN already
                                        } else {
                                            // redirect to PIN input page
                                            intent = new Intent(MainActivity.this, PinActivity.class);
                                        }

                                        // redirect to appropriate activity page
                                        startActivity(intent);

                                        // end splash screen activity
                                        finish();
                                    }
                                }
                            });

                } else {
                    // Intent for authenticated user
                    Intent intent;

                    // redirect to login activity page for user to log in the application
                    intent = new Intent(MainActivity.this, LoginActivity.class);

                    // redirect to appropriate activity page
                    startActivity(intent);

                    // end splash screen activity
                    finish();
                }
            }
        }, SPLASH_SCREEN_DELAY);
    }


    /**
     * Initializes Firebase components.
     */
    private void initFirebase () {
        // enable persistence for storing data in local cache for offline use
        if (!isPersistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }

        // get instance of Firebase Authentication
        this.mAuth = FirebaseAuth.getInstance();

        // get instance of Firebase Realtime Database
        this.database = FirebaseDatabase.getInstance();
    }
}
