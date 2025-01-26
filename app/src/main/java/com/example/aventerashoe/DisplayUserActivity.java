package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayUserActivity extends AppCompatActivity {

    private EditText editName, editAddress, editPhone, editshoesize;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);


// Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);




        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to homepage)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(DisplayUserActivity.this, Homepage.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });

        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        editshoesize = findViewById(R.id.editshoesize);
        saveButton = findViewById(R.id.saveButton);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            showLoginDialog();
            return; // Stop further execution if not logged in
        }





        String userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        fetchUserData();

        saveButton.setOnClickListener(v -> saveUserData());
    }


    private void showLoginDialog() {
        new AlertDialog.Builder(this)

                .setTitle("Login Required")
                .setMessage("You need to be logged in to access this page.")
                .setPositiveButton("Login", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class)); // Redirect to login
                    finish(); // Finish current activity
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(this, Homepage.class)); // Redirect to homepage
                    finish(); // Finish current activity
                })
                .setCancelable(false) // Prevent dismissing by clicking outside
                .show();
    }

    private void fetchUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        editName.setText(user.name);
                        editAddress.setText(user.address);
                        editPhone.setText(user.phone);
                        editshoesize.setText(user.shoesize);
                    }
                } else {
                    Toast.makeText(DisplayUserActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUserActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        String name = editName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String dept = editshoesize.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(dept)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User(name, address, auth.getCurrentUser().getEmail(), phone, dept);
        databaseReference.setValue(updatedUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DisplayUserActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DisplayUserActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
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
