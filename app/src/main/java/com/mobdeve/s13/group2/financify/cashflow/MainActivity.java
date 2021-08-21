package com.mobdeve.s13.group2.financify.cashflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.R;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private Button btnCashflowHome, btnRetry;
    private FirebaseAuth mAuth;
    private ProgressBar pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled (true);
        this.mAuth = FirebaseAuth.getInstance ();

        pbLogin = findViewById (R.id.pb_auth);
        btnCashflowHome = findViewById (R.id.btn_cashflow_home);
        btnRetry = findViewById (R.id.btn_retry);

        btnCashflowHome.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (MainActivity.this, CashflowHomeActivity.class);

                startActivity (i);
            };
        });

        btnRetry.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                btnCashflowHome.setVisibility (View.GONE);
                pbLogin.setVisibility (View.VISIBLE);
                mAuth.signOut();
                loginUser ();
            }
        });

        btnCashflowHome.setVisibility (View.GONE);
        pbLogin.setVisibility (View.VISIBLE);

        this.loginUser ();
    }

    private void loginUser () {
        String email = "dre@gmail.com";
        String pw = "cashflow123";

        if (this.mAuth.getCurrentUser() == null) {
            this.mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        btnCashflowHome.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        btnRetry.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }

                    pbLogin.setVisibility(View.GONE);
                }
            });
        } else {
            btnCashflowHome.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Offline authentication successful!", Toast.LENGTH_SHORT).show();
            pbLogin.setVisibility(View.GONE);
        }

//        FirebaseAuth.getInstance().signOut();
    }
}