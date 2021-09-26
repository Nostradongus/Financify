package com.mobdeve.s13.group2.financify;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.model.Model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * For settings activity / page, viewing and editing of logged in user's account details.
 */
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
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private EditText etOldPassword;

    // TextView for password fields
    private TextView tvOldPassword;
    private TextView tvNewPassword;
    private TextView tvConfirmPassword;

    // progress bar
    private ProgressBar pbSettings;

    // user account data
    private String firstName;
    private String lastName;
    private String email;
    private String currentPassword;

    // buttons
    private Button btnSave;
    private Button btnChangePassword;

    // Firebase components
    private FirebaseAuth mAuth;
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
        // get instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // get current user
        user = mAuth.getCurrentUser();

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
        this.etNewPassword = findViewById(R.id.et_settings_new_password);
        this.etOldPassword = findViewById(R.id.et_settings_old_password);
        this.etConfirmPassword = findViewById (R.id.et_settings_confirm_password);
        this.btnSave = findViewById(R.id.btn_settings_save);
        this.btnChangePassword = findViewById (R.id.btn_settings_change_password);
        this.pbSettings = findViewById(R.id.pb_settings);
        this.tvOldPassword = findViewById(R.id.tv_settings_old_password_label);
        this.tvNewPassword = findViewById(R.id.tv_settings_new_password_label);
        this.tvConfirmPassword = findViewById(R.id.tv_settings_confirm_password_label);

        // hide password fields by default
        showPasswordFields (false);

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
                    // check if new email was inputted, to verify if email is unique or not
                    String newEmail = etEmail.getText().toString().trim();
                    if (!email.equalsIgnoreCase(newEmail)) {
                        // check if new email inputted already exists in FirebaseAuth database or not
                        mAuth.fetchSignInMethodsForEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<SignInMethodQueryResult> task) {
                                        // if new email inputted does not yet exist in the FirebaseAuth database
                                        if (Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty()) {
                                            // proceed to re-authenticating user before updating account details
                                            reauthenticateAndUpdate();
                                        }
                                        // else, indicate to user that new email inputted already exists
                                        else {
                                            Toast.makeText(
                                                    SettingsActivity.this,
                                                    "Email already exists! Please try again.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                                });
                    }
                    // else, proceed to re-authenticating user before updating account details
                    else {
                        reauthenticateAndUpdate();
                    }
                }
            }
        });

        // set listener for show change password fields button
        this.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show fields
                showPasswordFields (true);
            }
        });

        // get and update input fields with user data from database
        getUserData();
    }

    /**
     * Re-authenticates user before updating account settings.
     */
    private void reauthenticateAndUpdate() {
        // get input for current password
        currentPassword = etOldPassword.getText().toString().trim();

        // hide Buttons and show ProgressBar
        btnSave.setVisibility (View.GONE);
        btnChangePassword.setVisibility (View.GONE);
        pbSettings.setVisibility (View.VISIBLE);

        // get credentials of user
        AuthCredential credentials = EmailAuthProvider
                .getCredential(email, currentPassword);

        // re-authenticate user to update account settings
        user.reauthenticate(credentials)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUserAccount();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        String toastError;

                        // if too many requests are being sent
                        if (e instanceof FirebaseTooManyRequestsException) {
                            toastError = "Too many requests! Please try again later.";
                            // if credentials are invalid
                        } else if (e instanceof  FirebaseAuthInvalidCredentialsException) {
                            toastError = "Invalid password! Please try again.";
                            etOldPassword.setError("Invalid password.");
                            etOldPassword.requestFocus();
                            // if other errors occur
                        } else {
                            toastError = "[" + e.getClass().getSimpleName() + "] An error occurred!";
                        }

                        // show Toast
                        Toast.makeText (
                                SettingsActivity.this,
                                toastError,
                                Toast.LENGTH_SHORT
                        ).show ();

                        // show Buttons and hide ProgressBar
                        btnSave.setVisibility(View.VISIBLE);

                        if (etNewPassword.getVisibility() == View.VISIBLE)
                            btnChangePassword.setVisibility(View.INVISIBLE);
                        else
                            btnChangePassword.setVisibility(View.VISIBLE);

                        pbSettings.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Gets user's data from Firebase database and updates the input fields with it.
     */
    private void getUserData() {
        // get data from database
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                // retrieve data from database
                DataSnapshot snapshot = task.getResult();

                // store acquired user account data from database
                firstName = snapshot.child("firstName").getValue(String.class);
                lastName = snapshot.child("lastName").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);

                // update input fields with acquired data
                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etEmail.setText(email);
                etOldPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            }
        });
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        String firstNameInput = this.etFirstName.getText().toString().trim();
        String lastNameInput = this.etLastName.getText().toString().trim();
        String emailInput = this.etEmail.getText().toString().trim();
        String oldPasswordInput = this.etOldPassword.getText().toString().trim();
        String newPasswordInput = this.etNewPassword.getText().toString().trim();
        String confirmPasswordInput = this.etConfirmPassword.getText().toString().trim();

        // if first name field is empty
        if (firstNameInput.isEmpty()) {
            this.etFirstName.setError("Please input your first name.");
            this.etFirstName.requestFocus();
            return false;
        }
        // if last name field is empty
        else if (lastNameInput.isEmpty()) {
            this.etLastName.setError("Please input your last name.");
            this.etLastName.requestFocus();
            return false;
        }
        // if email field is empty
        else if (emailInput.isEmpty()) {
            this.etEmail.setError("Please input your email.");
            this.etEmail.requestFocus();
            return false;
        }
        // if email input is not valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            this.etEmail.setError("Please input a valid email address.");
            this.etEmail.requestFocus();
            return false;
        }
        // if old password field is empty
        else if (oldPasswordInput.isEmpty()) {
            this.etOldPassword.setError("Please enter your current password.");
            this.etOldPassword.requestFocus();
            return false;
        }
        // if password field is not empty (i.e., user wants to change password)
        else if (!newPasswordInput.isEmpty()) {
            // if password is less than 6 characters
            if (newPasswordInput.length() < 6) {
                this.etNewPassword.setError("Password should be greater than 6 characters.");
                this.etNewPassword.requestFocus();
                return false;
            }
            // if there is no password confirmation
            else if (confirmPasswordInput.isEmpty()) {
                this.etConfirmPassword.setError("Please confirm your password.");
                this.etConfirmPassword.requestFocus();
                return false;
            }
            // if passwords do not match
            else if (!newPasswordInput.equals (confirmPasswordInput)) {
                this.etNewPassword.setError("Passwords do not match.");
                this.etConfirmPassword.setError("Passwords do not match.");
                this.etNewPassword.requestFocus();
                return false;
            }
        }

        // input fields are valid
        return true;
    }

    /**
     * Updates the account data and details of the user depending on what was changed.
     */
    private void updateUserAccount() {
        // indicator if data has been updated
        boolean updated = false;

        // get edit text input values
        String newFirstName = etFirstName.getText().toString().trim();
        String newLastName = etLastName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

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

        // if email or password has been changed
        if (!email.equalsIgnoreCase(newEmail) || !newPassword.isEmpty()) {
            // change and update user's email first in the database
            if (!email.equalsIgnoreCase(newEmail)) {
                dbRef.child("email").setValue(newEmail);

                // update email first in Firebase Authentication database
                updateUserEmailAuthentication(newEmail, newPassword);
            }
            // change and update user's password instead in the database
            else {
                // Password no longer stored in Realtime DB
                dbRef.child("password").setValue(null);

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
                btnSave.setVisibility(View.VISIBLE);
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
//        AuthCredential credentials = EmailAuthProvider
//                .getCredential(email, currentPassword);

        // re-authenticate user to update email
//        user.reauthenticate(credentials)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        // change and update user's email
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        // update email with new data
                                        email = newEmail;

                                        // if password has been changed on input
                                        if (!newPassword.isEmpty() && !currentPassword.equalsIgnoreCase(newPassword)) {
                                            // Password no longer stored in Realtime DB
                                            dbRef.child("password").setValue(null);

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
//                    }
//                });
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull @NotNull Exception e) {
//                        // print error in console
//                        e.printStackTrace();
//
//                        // indicate user that something has gone in the process
//                        Toast.makeText(
//                                SettingsActivity.this,
//                                "An error has occurred, please try again.",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                });
    }

    /**
     * Updates the user's password in Firebase Authentication list database.
     */
    private void updateUserPasswordAuthentication(String newPassword) {
        // get credentials of user
//        AuthCredential credentials = EmailAuthProvider
//                .getCredential(email, currentPassword);

        // re-authenticate user to update password
//        user.reauthenticate(credentials)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<Void> task) {
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
//                    }
//                });
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull @NotNull Exception e) {
//                        // print error in console
//                        e.printStackTrace();
//
//                        // indicate user that something has gone in the process
//                        Toast.makeText(
//                                SettingsActivity.this,
//                                "An error has occurred, please try again.",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                });
    }

    /**
     * Indicates to user that data are changed and updated successfully.
     * and redirects back to home page after data update.
     */
    private void updateSuccessful() {
        // turn off progress bar
        this.pbSettings.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);

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
     * Shows the fields needed for changing the user's password.
     */
    private void showPasswordFields (boolean show) {
        if (show) {
            btnChangePassword.setVisibility (View.GONE);
            etNewPassword.setVisibility (View.VISIBLE);
            etConfirmPassword.setVisibility (View.VISIBLE);
            tvNewPassword.setVisibility (View.VISIBLE);
            tvConfirmPassword.setVisibility (View.VISIBLE);
        } else {
            btnChangePassword.setVisibility (View.VISIBLE);
            etNewPassword.setVisibility (View.GONE);
            etConfirmPassword.setVisibility (View.GONE);
            tvNewPassword.setVisibility (View.GONE);
            tvConfirmPassword.setVisibility (View.GONE);
        }
    }


    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Home and finishes this activity.
     */
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