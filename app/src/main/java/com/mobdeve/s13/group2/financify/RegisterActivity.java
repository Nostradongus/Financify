package com.mobdeve.s13.group2.financify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.model.Model;
import com.mobdeve.s13.group2.financify.model.User;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

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
        initFirebase();

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
        this.pbRegister = findViewById(R.id.pb_register);

        registerBtn = findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // check if fields are empty and validate email and password fields as well
                if (!checkEmpty(firstName, lastName, email, password) &&
                    validateEmailAndPassword(email, password)) {
                    // disable register button
                    registerBtn.setEnabled (false);
                    registerBtn.setVisibility (View.INVISIBLE);

                    // register new user to the database
                    registerUser(new User(firstName, lastName, email, password));
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
    private boolean validateEmailAndPassword(String email, String password) {
        boolean valid = true;

        // check if email is in valid format
        String[] atFormat = email.split("@");
        String[] dotFormat = email.split("\\.");
        if (atFormat.length < 2 || dotFormat.length < 2) {
            // set error message
            this.etEmail.setError("Please input a valid email");
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

        return valid;
    }

    /**
     * Creates and registers the user to the application's database.
     *
     * @param   user    the user's details
     */
    private void registerUser(User user) {
        // show register progress bar as new user data is being added to database
        this.pbRegister.setVisibility(View.VISIBLE);

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
                                    registerBtn.setEnabled (true);
                                    registerBtn.setVisibility (View.VISIBLE);
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
        this.pbRegister.setVisibility(View.GONE);

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
        this.pbRegister.setVisibility(View.GONE);

        // alert user that error has occurred during registration process
        Toast.makeText(
                this,
                R.string.register_failed,
                Toast.LENGTH_SHORT
        ).show();
    }
}