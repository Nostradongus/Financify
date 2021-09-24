package com.mobdeve.s13.group2.financify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.helpers.Keys;
import com.mobdeve.s13.group2.financify.model.User;
import com.mobdeve.s13.group2.financify.pin.RegisterPINActivity;

import org.jetbrains.annotations.NotNull;

/**
 * For register activity / page, for the user to create an account with the given required details.
 */
public class RegisterActivity extends AppCompatActivity {

    // first name input field
    private EditText etFirstName;

    // last name input field
    private EditText etLastName;

    // email input field
    private EditText etEmail;

    // password input field
    private EditText etPassword;

    // confirm password field
    private EditText etConfirmPassword;

    // progress bar when register button is clicked
    private ProgressBar pbRegister;

    // register button
    private Button registerBtn;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize Firebase components
        initFirebase ();
        // initialize view components
        initComponents();
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
    private void initComponents() {
        // initialize register view components
        this.etFirstName = findViewById(R.id.et_register_firstname);
        this.etLastName = findViewById(R.id.et_register_lastname);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.etConfirmPassword = findViewById (R.id.et_register_confirm_password);
        this.pbRegister = findViewById(R.id.pb_register);

        registerBtn = findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // check if fields are empty and validate email and password fields as well
                if (!checkEmpty(firstName, lastName, email, password) &&
                    validateEmailAndPassword(email, password, confirmPassword)) {
                    // hide Button and show ProgressBar
                    registerBtn.setEnabled (false);
                    registerBtn.setVisibility (View.INVISIBLE);
                    pbRegister.setVisibility (View.VISIBLE);

                    // create user account in Firebase Auth
                    mAuth.createUserWithEmailAndPassword (email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult> () {
                                @Override
                                public void onComplete (@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful ()) {
                                        registerSuccess();
                                        // proceed to PIN creation page
                                        Intent i = new Intent (RegisterActivity.this, RegisterPINActivity.class);
                                        i.putExtra (Keys.KEY_PIN_USER, new User (firstName, lastName, email));
                                        i.putExtra (Keys.KEY_PIN_NEW, true);
                                        // launch activity
                                        startActivity (i);
                                        // finish activity
                                        finish ();
                                    } else {
                                        try {
                                            registerFailed (task.getException().getClass().getSimpleName());
                                        } catch (NullPointerException npe) {
                                            npe.printStackTrace ();
                                            registerFailed ("");
                                        }
                                    }

                                    // show Button
                                    registerBtn.setEnabled (true);
                                    registerBtn.setVisibility (View.VISIBLE);
                                }
                            });

                }
            }
        });

        Button loginRedirectBtn = findViewById(R.id.btn_login_redirect);
        loginRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect back to login activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                // end register activity
                finish();
            }
        });
    }

    /**
     * Checks if the required input fields are not empty.
     *
     * @param   firstName   user's first name
     * @param   lastName    user's last name
     * @param   email       user's email address
     * @param   password    user's desired password
     *
     * @return  true if input fields are not empty, false otherwise.
     */
    private boolean checkEmpty(String firstName, String lastName, String email, String password) {
        boolean hasEmpty = false;

        // if first name input field is empty
        if (firstName.isEmpty()) {
            // set error message
            this.etFirstName.setError("Please input your first name");
            this.etFirstName.requestFocus();
            hasEmpty = true;
        }
        // if last name input field is empty
        else if (lastName.isEmpty()) {
            this.etLastName.setError("Please input your last name");
            this.etLastName.requestFocus();
            hasEmpty = true;
        }
        // if email input field is empty
        else if (email.isEmpty()) {
            this.etEmail.setError("Please input your email address");
            this.etEmail.requestFocus();
            hasEmpty = true;
        }
        // if password input field is empty
        else if (password.isEmpty()) {
            this.etPassword.setError("Please input your password");
            this.etPassword.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }

    /**
     * Checks if the inputted email and password values are of valid format.
     *
     * @param   email       the user's email
     * @param   password    the user's desired password
     *
     * @return  true if the inputted email and password are of valid format, false otherwise.
     */
    private boolean validateEmailAndPassword(String email, String password, String confirmPassword) {
        boolean valid = true;

        // check if email is in valid format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // set error message
            this.etEmail.setError("Please input a valid email address");
            this.etEmail.requestFocus();
            valid = false;
        }
        // check if password is more than or equal to 6 characters as restricted by Firebase Auth
        else if (password.length() < 6) {
            // set error message
            this.etPassword.setError("Password must be more than or equal to 6 characters");
            this.etPassword.requestFocus();
            valid = false;
        }
        // if there is no password confirmation
        else if (confirmPassword.isEmpty()) {
            this.etConfirmPassword.setError("Please confirm your password.");
            this.etConfirmPassword.requestFocus();
            valid = false;
        }
        // if passwords do not match
        else if (!password.equals (confirmPassword)) {
            this.etPassword.setError("Passwords do not match!");
            this.etConfirmPassword.setError("Passwords do not match!");
            this.etPassword.requestFocus();
            valid = false;
        }

        return valid;
    }

    /**
     * Indicates success of registration to the user and redirects back to the login page
     * afterwards.
     */
    private void registerSuccess() {
        // disable register progress bar as process is complete
        this.pbRegister.setVisibility(View.GONE);

        // log out temporarily logged in user
        this.mAuth.signOut();

        // alert user that registration was successful
        Toast.makeText(
                this,
                R.string.register_success,
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Indicates error or failure of registration to the user.
     */
    private void registerFailed(String exceptionName) {
        // disable register progress bar as process is complete
        this.pbRegister.setVisibility(View.GONE);

        // create error toast text
        String errorToast = "Registration Failed!";

        if (exceptionName.equalsIgnoreCase ("FirebaseAuthUserCollisionException")) {
            errorToast += " Email already taken!";
            etEmail.setError ("Email already taken!");
        }

        // alert user that error has occurred during registration process
        Toast.makeText(
                this,
                errorToast,
                Toast.LENGTH_SHORT
        ).show();
    }
}