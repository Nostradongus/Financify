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

// TODO: add documentation
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

    private void initFirebase() {
        // get instance of Firebase Authentication
        this.mAuth = FirebaseAuth.getInstance();

        // get instance of Firebase Realtime Database
        this.database = FirebaseDatabase.getInstance();
    }

    private void initComponents() {
        // initialize register view components
        this.etFirstName = findViewById(R.id.et_register_firstname);
        this.etLastName = findViewById(R.id.et_register_lastname);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.pbRegister = findViewById(R.id.pb_register);

        Button registerBtn = findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // check if fields are empty
                if (!checkEmpty(firstName, lastName, email, password)) {
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
                                    // disable register progress bar as process is complete
                                    pbRegister.setVisibility(View.GONE);

                                    // if new user account data was added successfully to the database
                                    if (task.isSuccessful()) {
                                        registerSuccess();
                                    } else {
                                        registerFailed();
                                    }
                                }
                            });
                        } else {
                            registerFailed();
                        }
                    }
                });
    }

    private void registerSuccess() {
        // alert user that registration was successful
        Toast.makeText(
                this,
                "User Registered Successfully!",
                Toast.LENGTH_SHORT
        ).show();

        // redirect back to login page afterwards
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // end register activity
        finish();
    }

    private void registerFailed() {
        // alert user that error has occurred during registration process
        Toast.makeText(
                this,
                "User Registration Failed!",
                Toast.LENGTH_SHORT
        ).show();
    }
}