package com.mobdeve.s13.group2.financify;

import android.content.Intent;
import android.os.Bundle;
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

import org.jetbrains.annotations.NotNull;

// TODO: add documentation
public class LoginActivity extends AppCompatActivity {

    // user email input field
    private EditText etEmail;

    // user password input field
    private EditText etPassword;

    // progress bar when login button is pressed
    private ProgressBar pbLogin;

    // Firebase components
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize Firebase components
        initFirebase();

        // initialize login activity view components
        initComponents();
    }

    private void initFirebase() {
        // get instance of Firebase Authentication
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initComponents() {
        // initialize text fields and progress bar functionalities
        this.etEmail = findViewById(R.id.et_login_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.pbLogin = findViewById(R.id.pb_login);

        // initialize login button functionality
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get email and password inputs from text fields
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // validate input fields
                if (!checkEmpty(email, password)) {
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

    private void loginUser(String email, String password) {
        // show login progress bar during the accessing of user authentication data
        this.pbLogin.setVisibility(View.VISIBLE);

        // login user using Firebase Authentication
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        // disable login progress bar as authentication process is complete
                        pbLogin.setVisibility(View.GONE);

                        // if inputted credentials are valid and user was able to login
                        if (task.isSuccessful()) {
                            // redirect to home page
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);

                            // end login activity
                            finish();
                        } else {
                            // inputted credentials were not valid, show error message to user
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid username or password!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }
}