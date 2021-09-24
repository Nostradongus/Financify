package com.mobdeve.s13.group2.financify.pin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.helpers.BCryptHelper;
import com.mobdeve.s13.group2.financify.helpers.BiometricHelper;
import com.mobdeve.s13.group2.financify.model.Model;

import android.content.Intent;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class PinActivity extends AppCompatActivity {

    // UI components
    private Button btnConfirm;
    private Button btnUseBiometrics;
    private EditText etPin;
    private ProgressBar pbConfirm;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // Biometrics
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        // initialize Biometrics components
        initBiometrics ();
        // initialize Firebase components
        initFirebase ();
        // initialize UI components
        initComponents ();
        // show biometric prompt, if device supports it
        showBiometricPrompt();
    }

    /**
     * Initializes Firebase components.
     */
    private void initFirebase() {
        // get instance of Firebase Authentication
        this.mAuth = FirebaseAuth.getInstance();

        // get instance of Firebase Realtime Database
        this.database = FirebaseDatabase.getInstance();
    }

    /**
     * Initializes UI components.
     */
    private void initComponents () {
        // retrieve IDs
        btnConfirm = findViewById (R.id.btn_pin_confirm);
        btnUseBiometrics = findViewById (R.id.btn_pin_use_bio);
        etPin = findViewById (R.id.et_pin_input);
        pbConfirm = findViewById (R.id.pb_pin);

        // if device supports biometric authentication
        if (BiometricHelper.canUseBiometricAuth (this)) {
            // OnClickListener for use biometrics button
            btnUseBiometrics.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    // show biometric prompt
                    showBiometricPrompt ();
                }
            });
        // else
        } else {
            // hide and disable button
            btnUseBiometrics.setVisibility (View.INVISIBLE);
            btnUseBiometrics.setEnabled (false);
        }

        // OnClickListener for confirm PIN button
        btnConfirm.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // check first if PIN field is valid
                if (isValidField ()) {
                    // show ProgressBar and hide Button
                    btnConfirm.setEnabled (false);
                    btnConfirm.setVisibility (View.INVISIBLE);
                    pbConfirm.setVisibility (View.VISIBLE);

                    // retrieve user PIN from Firebase
                    database.getReference (Model.users.name ())
                            .child (mAuth.getCurrentUser ().getUid ())
                            .child (Model.pin.name ())
                            .get ()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful ()) {
                                        // retrieve PIN input
                                        String etPinVal = etPin.getText ().toString ().trim ();

                                        // check if PIN is valid
                                        if (BCryptHelper.isCorrectPIN (etPinVal, task.getResult ().getValue ().toString ())) {
                                            // show success message to user
                                            Toast.makeText (
                                                    PinActivity.this,
                                                    "Logged In Successfully!",
                                                    Toast.LENGTH_SHORT
                                            ).show ();

                                            // Intent for Homepage
                                            Intent i = new Intent(PinActivity.this, HomeActivity.class);
                                            // launch activity
                                            startActivity (i);
                                            // finish activity
                                            finish ();
                                        } else {
                                            // show error to user
                                            etPin.setError ("Wrong PIN!");
                                        }

                                        // hide ProgressBar and show Button
                                        btnConfirm.setEnabled (true);
                                        btnConfirm.setVisibility (View.VISIBLE);
                                        pbConfirm.setVisibility (View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Validation for PIN input field.
     *
     * @return true if field is valid; otherwise false
     */
    private boolean isValidField () {
        String inputPIN = etPin.getText ().toString ().trim ();

        // if field is empty
        if (inputPIN.isEmpty ()) {
            etPin.setError ("Please enter your PIN!");
            return false;
        }

        // if PIN is not 6 characters long
        if (inputPIN.length () != 6) {
            etPin.setError ("Invalid PIN!");
            return false;
        }

        return true;
    }

    /**
     * Initialize Biometrics components.
     */
    private void initBiometrics () {
        // executor
        executor = ContextCompat.getMainExecutor (this);

        // BiometricPrompt itself with its listener
        biometricPrompt = new BiometricPrompt(PinActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull @NotNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                System.out.println ("Authentication Error: " + errString + " with code " + errorCode);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull @NotNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText (PinActivity.this, "Authenticated successfully!", Toast.LENGTH_SHORT).show ();

                // create intent going to HomeActivity
                Intent i = new Intent (PinActivity.this, HomeActivity.class);
                // launch activity
                startActivity (i);
                // finish this activity
                finish ();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                System.out.println ("Authentication Failure!");
            }
        });

        // biometric prompt properties
        promptInfo = new BiometricPrompt.PromptInfo.Builder ()
                .setTitle ("Biometric Authentication")
                .setSubtitle ("Login using biometrics")
                .setNegativeButtonText ("Enter PIN instead")
                .build ();
    }

    /**
     * Show biometric prompt if device supports it.
     */
    private void showBiometricPrompt () {
        // if device can use biometric authentication
        if (BiometricHelper.canUseBiometricAuth (this)) {
            // show biometric prompt
            biometricPrompt.authenticate (promptInfo);
        }
    }
}