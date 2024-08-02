package com.anjacarchistra.kvm.stratafest;


import static com.anjacarchistra.kvm.stratafest.api.Constants.EMAIL_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PASSWORD_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anjacarchistra.kvm.stratafest.api.LoginHandler;

public class Login extends AppCompatActivity {

     private static final String  TAG= "LoginActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch the values
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate email and password
                if (validateEmail(email) && validatePassword(password)) {
                    // Log the values if valid
                    Log.d(TAG, "Email: " + email);
                    Log.d(TAG, "Password: " + password);

                   // new LoginHandler(Login.this, Login.this, email, password).execute();

                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(EMAIL_KEY, email);
                    editor.putString(PASSWORD_KEY, password);
                    editor.apply();
                    Toast.makeText(Login.this, "VAlues added to shared preferences", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this,Dashboard.class));

                }

            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }
}