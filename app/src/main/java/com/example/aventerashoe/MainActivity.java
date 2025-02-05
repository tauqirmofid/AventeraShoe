package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if admin is logged in
        SharedPreferences adminPrefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        boolean isAdminLoggedIn = adminPrefs.getBoolean("isAdminLoggedIn", false);
        if (isAdminLoggedIn) {
            // Redirect to AdminActivity for admins
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent back navigation
            return;
        }

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Handle Login
        loginButton.setOnClickListener(v -> loginUser());

        // Navigate to Register Activity
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.equals("admin") && password.equals("admin")) {
            // Save admin login state
            SharedPreferences adminPrefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
            SharedPreferences.Editor adminEditor = adminPrefs.edit();
            adminEditor.putBoolean("isAdminLoggedIn", true);
            adminEditor.apply();

            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to AdminActivity
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent navigating back
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            // Navigate to HomeActivity for regular users
                            Intent intent = new Intent(MainActivity.this, Homepage.class);
                            startActivity(intent);
                            finish(); // Cl  to prevent navigating back
                        } else {
                            Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}