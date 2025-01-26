package com.example.aventerashoe;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private Button submit;
    private Spinner deptSpinner;
    private String name, address, email, phone, password, shoesize;

    private LinearLayout inputLayout, outputLayout;
    private TextView outputText;
    private EditText nameEditText, idEditText, emailEditText, phoneEditText, passwordEditText;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name);
        idEditText = findViewById(R.id.sId);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.num);
        passwordEditText = findViewById(R.id.password);

        deptSpinner = findViewById(R.id.spinner);
        inputLayout = findViewById(R.id.inputLayout);
        outputLayout = findViewById(R.id.outputLayout);
        outputText = findViewById(R.id.outputText);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        String[] items = new String[]{"Select Size", "7", "8", "9", "10", "11", "12", "13"};
        deptSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shoesize = deptSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            name = nameEditText.getText().toString().trim();
            address = idEditText.getText().toString().trim();
            email = emailEditText.getText().toString().trim();
            phone = phoneEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();

            if (validateInputs()) {
                registerUser(email, password);
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        } else if (!Pattern.compile("[a-zA-Z._ ]+").matcher(name).matches()) {
            nameEditText.setError("Name can only contain alphabets");
            nameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            idEditText.setError("Address is required");
            idEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return false;
        }

        if (Objects.equals(shoesize, "Select Shoe size")) {
            Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid());
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration successful! Verification email sent. Verify your email to log in.", Toast.LENGTH_LONG).show();
                                            finish(); // Go back to login page
                                        } else {
                                            Toast.makeText(this, "Failed to send verification email. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(String userId) {
        User user = new User(name, address, email, phone, shoesize);
        databaseReference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save user data: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class User {
        public String name, address, email, phone, shoesize;

        public User() {}

        public User(String name, String address, String email, String phone, String shoesize) {
            this.name = name;
            this.address = address;
            this.email = email;
            this.phone = phone;
            this.shoesize = shoesize;
        }
    }
}
