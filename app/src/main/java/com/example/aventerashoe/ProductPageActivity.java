package com.example.aventerashoe;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductPageActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice;
    private Button btnOrderNow, btnStickyOrderNow;

    private String productId, productName, productImageUrl;
    private int productStock;
    private double productPrice;

    private DatabaseReference productDatabaseRef;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        // Initialize Views
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        btnOrderNow = findViewById(R.id.btn_order_now);
        btnStickyOrderNow = findViewById(R.id.btn_sticky_order_now);

        // Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);

        // ScrollView reference
        ScrollView scrollView = findViewById(R.id.scrollView);

        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());

        // Store logo click listener (Navigate to homepage)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ProductPageActivity.this, Homepage.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });

        // Get product ID from intent
        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Invalid product!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Reference
        productDatabaseRef = FirebaseDatabase.getInstance().getReference("Products");
        auth = FirebaseAuth.getInstance();
        // Load product details
        loadProductDetails();



        // Handle the main "Order Now" button click
        btnOrderNow.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                showOrderDialog(); // Show order dialog if logged in
            } else {
                showLoginDialog(); // Show login dialog if not logged in
            }
        });

        // Handle the sticky button click
        btnStickyOrderNow.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                showOrderDialog(); // Show order dialog if logged in
            } else {
                showLoginDialog(); // Show login dialog if not logged in
            }
        });









        // Listen to scroll changes to decide whether to show/hide the sticky button
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int[] location = new int[2];
            btnOrderNow.getLocationOnScreen(location);

            if (location[1] <= 0) {
                findViewById(R.id.sticky_button_container).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.sticky_button_container).setVisibility(View.GONE);
            }
        });
    }

    private void loadProductDetails() {
        productDatabaseRef.child(productId).get().addOnSuccessListener(snapshot -> {
            Product product = snapshot.getValue(Product.class);
            if (product != null) {
                productName = product.getName();
                productPrice = Double.parseDouble(product.getPrice());
                productStock = product.getStock();
                productImageUrl = product.getImageUrl();

                tvProductName.setText(productName);
                tvProductPrice.setText("BDT " + productPrice);
                btnStickyOrderNow.setText("Order Now (BDT " + productPrice + ")");

                Glide.with(this)
                        .load(productImageUrl)
                        .into(ivProductImage);
            } else {
                Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading product details.", Toast.LENGTH_SHORT).show();
        });
    }


    private void showLoginDialog() {
        new AlertDialog.Builder(this)

                .setTitle("Login Required")
                .setMessage("You need to be logged in to access To order.")
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

    private void showOrderDialog() {
        Dialog orderDialog = new Dialog(this);
        orderDialog.setContentView(R.layout.dialog_order);
        orderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        orderDialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);



        // Initialize dialog views
        EditText etName = orderDialog.findViewById(R.id.et_name);
        EditText etPhone = orderDialog.findViewById(R.id.et_phone);
        EditText etEmail = orderDialog.findViewById(R.id.et_email);
        EditText etAddress = orderDialog.findViewById(R.id.et_address);
        EditText etQuantity = orderDialog.findViewById(R.id.et_quantity);

        TextView tvTotalOrderValue = orderDialog.findViewById(R.id.tv_total_order_value);
        Spinner spinnerShoeSize = orderDialog.findViewById(R.id.spinner_shoe_size);
        Button btnPlaceOrder = orderDialog.findViewById(R.id.btn_place_order);
        Button btnIncreaseQuantity = orderDialog.findViewById(R.id.btn_increase_quantity);
        Button btnDecreaseQuantity = orderDialog.findViewById(R.id.btn_decrease_quantity);
        ImageView ivCloseDialog = orderDialog.findViewById(R.id.iv_close_dialog);
        CheckBox cbNameEditable = orderDialog.findViewById(R.id.cb_name_editable);
        CheckBox cbPhoneEditable = orderDialog.findViewById(R.id.cb_phone_editable);
        CheckBox cbAddressEditable = orderDialog.findViewById(R.id.cb_address_editable);



        // Pre-fill data from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etPhone.setText(snapshot.child("phone").getValue(String.class));
                    etAddress.setText(snapshot.child("address").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductPageActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();

            }
        });

    // Enable fields when checkboxes are checked
        cbNameEditable.setOnCheckedChangeListener((buttonView, isChecked) -> etName.setEnabled(isChecked));
        cbPhoneEditable.setOnCheckedChangeListener((buttonView, isChecked) -> etPhone.setEnabled(isChecked));
        cbAddressEditable.setOnCheckedChangeListener((buttonView, isChecked) -> etAddress.setEnabled(isChecked));









        // Set default quantity and price calculation
        etQuantity.setText("1");
        updateTotalOrderValue(etQuantity, tvTotalOrderValue);

        // Populate shoe size dropdown
        List<String> shoeSizes = new ArrayList<>();
        for (int i = 3; i <= 12; i++) {
            shoeSizes.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shoeSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShoeSize.setAdapter(adapter);

        // Increase quantity button
        btnIncreaseQuantity.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(etQuantity.getText().toString());
            etQuantity.setText(String.valueOf(currentQuantity + 1));
            updateTotalOrderValue(etQuantity, tvTotalOrderValue);
        });

        // Decrease quantity button
        btnDecreaseQuantity.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(etQuantity.getText().toString());
            if (currentQuantity > 1) {
                etQuantity.setText(String.valueOf(currentQuantity - 1));
                updateTotalOrderValue(etQuantity, tvTotalOrderValue);
            }
        });

        // Add TextWatcher for manual quantity entry
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalOrderValue(etQuantity, tvTotalOrderValue);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




        // Close dialog button
        ivCloseDialog.setOnClickListener(v -> orderDialog.dismiss());




        // Place order button
        btnPlaceOrder.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
           String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String shoeSize = String.valueOf(spinnerShoeSize.getTextDirection());

            if (name.isEmpty() || phone.isEmpty()  || address.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            if (quantity > productStock) {
                Toast.makeText(this, "Not enough stock available!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate total cost
            double totalCost = quantity * productPrice;

            // Update stock in Firebase
            productDatabaseRef.child(productId).child("stock").setValue(productStock - quantity);

            // Save order details
            DatabaseReference orderDatabaseRef = FirebaseDatabase.getInstance().getReference("Orders");
            String orderId = orderDatabaseRef.push().getKey();
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", orderId);
            orderDetails.put("name", name);
            orderDetails.put("phone", phone);

            orderDetails.put("email", email);
            orderDetails.put("address", address);
            orderDetails.put("quantity", quantity);
            orderDetails.put("shoeSize", shoeSize);
            orderDetails.put("status", "Pending");
            orderDetails.put("productName", productName);
            orderDetails.put("productImageUrl", productImageUrl); // Save image URL
            orderDetails.put("totalCost", totalCost); // Save total cost

            orderDatabaseRef.child(orderId).setValue(orderDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showOrderSuccessDialog();
                    orderDialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Show the dialog
        orderDialog.show();


    }

    private void showOrderSuccessDialog() {
        Dialog successDialog = new Dialog(this);
        successDialog.setContentView(R.layout.dialog_order_success);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initialize dialog views
        MaterialButton btnOk = successDialog.findViewById(R.id.btn_ok);
        MaterialButton btnSeeOrders = successDialog.findViewById(R.id.btn_see_orders);

        // Handle OK button click
        btnOk.setOnClickListener(v -> successDialog.dismiss());

        // Handle See Orders button click
        btnSeeOrders.setOnClickListener(v -> {
            successDialog.dismiss();
            Intent intent = new Intent(ProductPageActivity.this, ViewOrdersActivity.class);
            startActivity(intent); // Redirect to "View Orders" page
        });

        // Show dialog
        successDialog.show();
    }


    private void updateTotalOrderValue(EditText etQuantity, TextView tvTotalOrderValue) {
        try {
            int quantity = Integer.parseInt(etQuantity.getText().toString());
            if (quantity > 0) {
                double totalCost = quantity * productPrice;
                tvTotalOrderValue.setText(String.format("Total Order Value: BDT %.2f", totalCost));
            } else {
                tvTotalOrderValue.setText("Total Order Value: BDT 0.00");
            }
        } catch (NumberFormatException e) {
            tvTotalOrderValue.setText("Total Order Value: BDT 0.00");
        }
    }
}
