package com.mobdeve.s13.group2.financify.pin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Model;
import com.mobdeve.s13.group2.financify.model.User;
import com.mobdeve.s13.group2.financify.reminders.Keys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class RegisterPINActivity extends AppCompatActivity {

    // UI attributes
    private Button btnCreate;
    private EditText etPin, etPinConfirm;
    private ProgressBar pbCreate;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // User from Registration Activity
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_register);

        // initialize Firebase components
        initFirebase ();
        // initialize UI components
        initComponents ();
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
     * Initializes UI components
     */
    private void initComponents () {
        // retrieve IDs
        btnCreate = findViewById (R.id.btn_pin_create_confirm);
        etPin = findViewById (R.id.et_pin_create_input);
        etPinConfirm = findViewById (R.id.et_pin_confirm_input);
        pbCreate = findViewById (R.id.pb_pin_create);

        // retrieve User from Intent
        Intent i = getIntent ();
        user = i.getParcelableExtra (Keys.KEY_PIN_USER);
        boolean isNewUser = i.getBooleanExtra (Keys.KEY_PIN_NEW, true);

        // OnClickListener for Button
        btnCreate.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                // validate fields
                if (isValidPINFields ()) {
                    // retrieve PIN
                    String etPinVal = etPin.getText ().toString ();

                    // if new user is creating an account
                    if (isNewUser) {
                        // set user PIN
                        user.setUserPIN (etPinVal);

                        // register user in Firebase
                        registerUser (user);
                    // if existing user account has no PIN
                    } else {
                        // set user PIN in Firebase
                        database.getReference (Model.users.name ())
                                .child (mAuth.getCurrentUser ().getUid ())
                                .child (Model.pin.name ())
                                .setValue (etPinVal)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        // proceed to PIN input page
                                        Intent i = new Intent (RegisterPINActivity.this, PinActivity.class);
                                        // launch activity
                                        startActivity (i);
                                        // finish activity
                                        finish ();
                                    }
                                });
                    }
                } else {
                    Toast.makeText (
                            RegisterPINActivity.this,
                            "Please check all fields!",
                            Toast.LENGTH_SHORT
                    ).show ();
                }
            }
        });
    }

    /**
     * Verifies if all PIN fields are valid.
     *
     * @return true if valid; otherwise false
     */
    private boolean isValidPINFields () {
        String etPinVal = etPin.getText ().toString ().trim ();
        String etPinConfirmVal = etPinConfirm.getText ().toString ().trim ();

        // if main PIN is empty
        if (etPinVal.isEmpty ()) {
            etPin.setError ("PIN must not be empty!");
            return false;
        }

        // if PIN is not of length 6
        if (etPinVal.length () < 6) {
            etPin.setError ("PIN must be 6 characters long!");
            return false;
        }

        // if confirm PIN is empty
        if (etPinConfirmVal.isEmpty ()) {
            etPinConfirm.setError ("Please confirm your PIN!");
            return false;
        }

        // if PINs do not match
        if (!etPinConfirmVal.equalsIgnoreCase (etPinVal)) {
            etPin.setError ("PINs do not match!");
            return false;
        }

        // valid fields
        return true;
    }

    /**
     * Creates and registers the user to the application's database.
     *
     * @param   user    the user's details
     */
    private void registerUser(User user) {
        // disable register button
        btnCreate.setEnabled (false);
        btnCreate.setVisibility (View.INVISIBLE);

        // show register progress bar as new user data is being added to database
        this.pbCreate.setVisibility(View.VISIBLE);

        // register the user with Firebase
        this.mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        // if new user authentication data was added successfully to the database
                        if (task.isSuccessful()) {
                            // add user account data to users database collection
                            database.getReference(Model.users.name())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    // if new user account data was added successfully to the database
                                    if (task.isSuccessful()) {
                                        registerSuccess();
                                    } else {
                                        registerFailed();
                                    }

                                    // re-enable register button
                                    btnCreate.setEnabled (true);
                                    btnCreate.setVisibility (View.VISIBLE);
                                }
                            });
                        } else {
                            registerFailed();
                        }
                    }
                });
    }

    /**
     * Indicates success of registration to the user and redirects back to the login page
     * afterwards.
     */
    private void registerSuccess() {
        // disable register progress bar as process is complete
        this.pbCreate.setVisibility(View.GONE);

        // log out temporarily logged in user
        this.mAuth.signOut();

        // alert user that registration was successful
        Toast.makeText(
                this,
                R.string.register_success,
                Toast.LENGTH_SHORT
        ).show();

        // redirect back to login page afterwards
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // end register activity
        finish();
    }

    /**
     * Indicates error or failure of registration to the user.
     */
    private void registerFailed() {
        // disable register progress bar as process is complete
        this.pbCreate.setVisibility(View.GONE);

        // alert user that error has occurred during registration process
        Toast.makeText(
                this,
                R.string.register_failed,
                Toast.LENGTH_SHORT
        ).show();
    }
}