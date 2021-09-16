package com.mobdeve.s13.group2.financify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.cashflow.CashflowUpdateAccountActivity;
import com.mobdeve.s13.group2.financify.model.Model;

import org.jetbrains.annotations.NotNull;

// TODO: add documentation
public class SettingsActivity extends BaseActivity {

    // SharedPreferences for stored user first name and last name
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    // home button layout
    private ConstraintLayout clHomeBtn;

    // input fields
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;

    // progress bar
    private ProgressBar pbSettings;

    // user account data
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // save button
    private Button btnSave;

    // Firebase components
    private FirebaseUser user;
    private String userId;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize Firebase components
        initFirebase();

        // initialize UI components
        initComponents();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase() {
        // get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // if there is a user currently in session
        if (user != null) {
            userId = user.getUid();
            dbRef = FirebaseDatabase.getInstance().getReference()
                    .child(Model.users.name())
                    .child(userId);
            // If invalid session
        } else {
            goBackToLogin();
        }
    }

    /**
     * Initialize UI components.
     */
    private void initComponents() {
        // get shared preferences
        this.sp = getSharedPreferences("financify", Context.MODE_PRIVATE);
        this.spEditor = this.sp.edit();

        // initialize components
        this.clHomeBtn = findViewById(R.id.cl_settings_back_home_nav);
        this.etFirstName = findViewById(R.id.et_settings_first_name);
        this.etLastName = findViewById(R.id.et_settings_last_name);
        this.etEmail = findViewById(R.id.et_settings_email);
        this.etPassword = findViewById(R.id.et_settings_password);
        this.btnSave = findViewById(R.id.btn_settings_save);
        this.pbSettings = findViewById(R.id.pb_settings);

        // set listener for 'back to home' button
        this.clHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToHomePage();
            }
        });

        // set listener for save button for saving user account changes
        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if input fields are valid
                if (isValidForm()) {
                    updateUserAccount();
                }
            }
        });

        // get and update input fields with user data from database
        getUserData();
    }

    /**
     * Gets user's data from Firebase database and updates the input fields with it.
     */
    private void getUserData() {
        // get data from database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // store acquired user account data from database
                firstName = snapshot.child("firstName").getValue(String.class);
                lastName = snapshot.child("lastName").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);

                // update input fields with acquired data
                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etEmail.setText(email);
                etPassword.setText(password);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // leave blank...
            }
        });
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        // if first name field is empty
        if (this.etFirstName.getText().toString().trim().isEmpty()) {
            this.etFirstName.setError("Please input your first name.");
            this.etFirstName.requestFocus();
            return false;
        }
        // if last name field is empty
        else if (this.etLastName.getText().toString().trim().isEmpty()) {
            this.etLastName.setError("Please input your last name.");
            this.etLastName.requestFocus();
            return false;
        }
        // if email field is empty
        else if (this.etEmail.getText().toString().trim().isEmpty()) {
            this.etEmail.setError("Please input your email.");
            this.etEmail.requestFocus();
            return false;
        }
        // if email input is not valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(this.etEmail.getText().toString().trim()).matches()) {
            this.etEmail.setError("Please input a valid email address.");
            this.etEmail.requestFocus();
            return false;
        }
        // if password field is empty
        else if (this.etPassword.getText().toString().trim().isEmpty()) {
            this.etPassword.setError("Please input your password.");
            this.etPassword.requestFocus();
            return false;
        }
        // if password is less than 6 characters
        else if (this.etPassword.getText().toString().trim().length() < 6) {
            this.etPassword.setError("Password should be greater than 6 characters.");
            this.etPassword.requestFocus();
            return false;
        }

        // input fields are valid
        return true;
    }

    /**
     * Updates the account data and details of the user depending on what was changed.
     */
    private void updateUserAccount() {
        // disable save button
        btnSave.setEnabled(false);
        btnSave.setClickable(false);

        // turn on progress bar
        this.pbSettings.setVisibility(View.VISIBLE);

        // indicator if data has been updated
        boolean updated = false;

        // get edit text input values
        String newFirstName = etFirstName.getText().toString().trim();
        String newLastName = etLastName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();

        // if first name has been changed
        if (!firstName.equalsIgnoreCase(newFirstName)) {
            // change and update user's first name in the database
            dbRef.child("firstName").setValue(newFirstName);

            // update stored first name in shared preferences
            spEditor.putString("FIRSTNAME", newFirstName);

            updated = true;
        }

        // if last name has been changed
        if (!lastName.equalsIgnoreCase(newLastName)) {
            // change and update user's last name in the database
            dbRef.child("lastName").setValue(
                    etLastName.getText().toString().trim()
            );

            // update stored last name in shared preferences
            spEditor.putString("LASTNAME", newLastName);

            updated = true;
        }

        // if changes has been to first name, last name, or both, update shared preferences
        if (updated) {
            spEditor.apply();
        }

        if (!email.equalsIgnoreCase(newEmail) || !password.equalsIgnoreCase(newPassword)) {
            // change and update user's email first in the database
            if (!email.equalsIgnoreCase(newEmail)) {
                dbRef.child("email").setValue(newEmail);

                // update email first in Firebase Authentication database
                updateUserEmailAuthentication(newEmail, newPassword);
            }
            // change and update user's password instead in the database
            else {
                dbRef.child("password").setValue(newPassword);

                // update password in Firebase Authentication database
                updateUserPasswordAuthentication(newPassword);
            }
        }
        // else, check if some data from user's account has been updated
        else {
            // if some data has been updated
            if (updated) {
                // enable button save
                btnSave.setEnabled(true);
                btnSave.setClickable(true);
                // indicate user that data has been updated successfully
                // and return back to home page afterwards
                updateSuccessful();
            } else {
                this.pbSettings.setVisibility(View.GONE);
                // indicate user that no data has been changed due to unchanged
                // values from the input fields
                Toast.makeText(
                        this,
                        "No data has been changed.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    /**
     * Updates the user's email in Firebase Authentication list database.
     */
    private void updateUserEmailAuthentication(String newEmail, String newPassword) {
        // get credentials of user
        AuthCredential credentials = EmailAuthProvider
                .getCredential(email, password);

        // re-authenticate user to update email
        user.reauthenticate(credentials)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        // change and update user's email
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        // update email with new data
                                        email = newEmail;

                                        // if password has been changed on input
                                        if (!password.equalsIgnoreCase(newPassword)) {
                                            // update password in realtime database first
                                            dbRef.child("password").setValue(newPassword);

                                            // proceed to updating password in Firebase Authentication
                                            // database
                                            updateUserPasswordAuthentication(newPassword);
                                        }
                                        // else, redirect back to home page after update
                                        else {
                                            updateSuccessful();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        // print error in console
                                        e.printStackTrace();

                                        // indicate user that something has gone in the process
                                        Toast.makeText(
                                                SettingsActivity.this,
                                                "An error has occurred, please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        // print error in console
                        e.printStackTrace();

                        // indicate user that something has gone in the process
                        Toast.makeText(
                                SettingsActivity.this,
                                "An error has occurred, please try again.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Updates the user's password in Firebase Authentication list database.
     */
    private void updateUserPasswordAuthentication(String newPassword) {
        // get credentials of user
        AuthCredential credentials = EmailAuthProvider
                .getCredential(email, password);

        // re-authenticate user to update password
        user.reauthenticate(credentials)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        // change and update user's password
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        // indicate success and return back to home page
                                        updateSuccessful();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        // print error in console
                                        e.printStackTrace();

                                        // indicate user that something has gone in the process
                                        Toast.makeText(
                                                SettingsActivity.this,
                                                "An error has occurred, please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        // print error in console
                        e.printStackTrace();

                        // indicate user that something has gone in the process
                        Toast.makeText(
                                SettingsActivity.this,
                                "An error has occurred, please try again.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Indicates to user that data are changed and updated successfully.
     * and redirects back to home page after data update.
     */
    private void updateSuccessful() {
        // turn off progress bar
        this.pbSettings.setVisibility(View.GONE);

        // indicate user that data has been updated successfully
        Toast.makeText(
                this,
                "Account Updated Successfully.",
                Toast.LENGTH_SHORT
        ).show();

        // redirect back to home page
        goBackToHomePage();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (this, LoginActivity.class);
        startActivity (i);
        finish ();
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