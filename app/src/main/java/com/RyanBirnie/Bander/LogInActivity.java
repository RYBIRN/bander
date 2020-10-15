package com.RyanBirnie.Bander;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private FirebaseAuth auth;
    private Button logIn;
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Check to see if a user is already logged in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        // this sets up on click listener for the create Account button
        createAccount = (Button) findViewById(R.id.createAccountButton);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUnamePword();

            }
        });

        logIn = (Button) findViewById(R.id.logInButton);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter an email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please Enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // If login failed
                            Toast.makeText(LogInActivity.this, "Authentication Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
//                goToProfile();
            }
        });
    }

    public void createUnamePword() {
        Intent intent = new Intent(this, CreateAccountActivity.class);

        startActivity(intent);
    }

//    public void goToProfile() {
//        Intent intent = new Intent(this, MainActivity.class);
//
//        startActivity(intent);
//    }
}