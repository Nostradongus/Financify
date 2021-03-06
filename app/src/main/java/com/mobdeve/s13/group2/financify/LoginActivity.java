package com.mobdeve.s13.group2.financify;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.model.Model;

import org.jetbrains.annotations.NotNull;

/**
 * For login activity / page, for user to login his or her account to access the application's
 * features.
 */
public class LoginActivity extends AppCompatActivity {

    // SharedPreferences for storing logged in user data (session-like)
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    // user email input field
    private EditText etEmail;

    // user password input field
    private EditText etPassword;

    // progress bar when login button is pressed
    private ProgressBar pbLogin;

    // login button
    private Button loginBtn;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    // indicator if user is logged in
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize Firebase components
        initFirebase();

        // initialize login activity view components
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
     * Initializes UI components.
     */
    private void initComponents() {
        // indicator if user is logged in
        isLoggedIn = false;

        // initialize text fields and progress bar functionalities
        this.etEmail = findViewById(R.id.et_login_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.pbLogin = findViewById(R.id.pb_login);

        // initialize login button functionality
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get email and password inputs from text fields
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // validate input fields
                if (!checkEmpty(email, password) &&
                    validateEmailAndPassword(email, password)) {
                    // disable login button
                    loginBtn.setEnabled (false);
                    loginBtn.setVisibility (View.INVISIBLE);

                    // login user with inputted credentials
                    loginUser(email, password);
                }
            }
        });

        // initialize register redirect button functionality
        Button registerRedirectBtn = findViewById(R.id.btn_register_redirect);
        registerRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to register activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                // end login activity
                finish();
            }
        });
    }

    /**
     * Checks if the required input fields are not empty.
     *
     * @param   email       user's email
     * @param   password    user's password
     *
     * @return  true if all required input fields are not empty, false otherwise.
     */
    private boolean checkEmpty(String email, String password) {
        boolean hasEmpty = false;

        // if email input is empty
        if (email.isEmpty()) {
            // set error message to email input field
            this.etEmail.setError("Please input your email");
            this.etEmail.requestFocus();
            hasEmpty = true;
        }
        // if password input is empty
        else if (password.isEmpty()) {
            // set error message to password input field
            this.etPassword.setError("Please input your password");
            this.etPassword.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }

    /**
     * Checks if the inputted email and password are of valid format.
     *
     * @param   email       the user's email
     * @param   password    the user's password
     *
     * @return  true if both email and password are of valid format, false otherwise.
     */
    private boolean validateEmailAndPassword(String email, String password) {
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

        return valid;
    }

    /**
     * Logs in the user to the application with the given inputs.
     *
     * @param   email       the user's email
     * @param   password    the user's password
     */
    private void loginUser(String email, String password) {
        // show login progress bar during the accessing of user authentication data
        this.pbLogin.setVisibility(View.VISIBLE);

        // login user using Firebase Authentication
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        // if inputted credentials are valid and user was able to login
                        if (task.isSuccessful()) {
                            loginSuccess();
                        } else {
                            loginFailed();
                        }

                        // re-enable login button
                        loginBtn.setEnabled (true);
                        loginBtn.setVisibility (View.VISIBLE);
                    }
                });
    }

    /**
     * Indicates login success to the user and redirects to the home page of the application
     * afterwards.
     */
    private void loginSuccess() {
        // get logged in user's data
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference users = database.getReference().child(Model.users.name()).child(userId);
        users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (!isLoggedIn) {
                            // user is logged in
                            isLoggedIn = true;

                            // get first name and last name of user
                            String firstName = snapshot.child("firstName").getValue().toString();
                            String lastName = snapshot.child("lastName").getValue().toString();

                            // store user's firstname and lastname in SharedPreferences
                            sharedPreferences = getSharedPreferences("financify", Context.MODE_PRIVATE);
                            spEditor = sharedPreferences.edit();
                            spEditor.putString("FIRSTNAME", firstName);
                            spEditor.putString("LASTNAME", lastName);
                            spEditor.apply();

                            // disable login progress bar as authentication process is complete
                            pbLogin.setVisibility(View.GONE);

                            // show success message to user
                            Toast.makeText(
                                    LoginActivity.this,
                                    R.string.login_success,
                                    Toast.LENGTH_SHORT
                            ).show();

                            // redirect to home page
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);

                            // end login activity
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });
    }

    /**
     * Indicates login error or failure to the user.
     */
    private void loginFailed() {
        // disable login progress bar as authentication process is complete
        this.pbLogin.setVisibility(View.GONE);

        // inputted credentials were not valid, show error message to user
        Toast.makeText(
                LoginActivity.this,
                R.string.login_failed,
                Toast.LENGTH_SHORT
        ).show();
    }
}