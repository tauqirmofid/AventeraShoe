package com.example.aventerashoe;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView imgProductDetail;
    private TextView tvProductName, tvProductPrice, tvProductDescription;
    private DatabaseReference databaseReference;

    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        imgProductDetail = findViewById(R.id.img_product_detail);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductDescription = findViewById(R.id.tv_product_description);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Get productId passed from the Intent
        productId = getIntent().getStringExtra("productId");

        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no productId is provided
            return;
        }

        // Load product details
        loadProductDetails();
    }

    private void loadProductDetails() {
        databaseReference.child(productId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    // Load data into views
                    Glide.with(this)
                            .load(product.getImageUrl())
                          //  .placeholder(R.drawable.placeholder_image) // Optional placeholder image
                    //        .error(R.drawable.error_image) // Optional error image
                            .into(imgProductDetail);

                    tvProductName.setText(product.getName());
                    tvProductPrice.setText("Price: $" + product.getPrice());
                    tvProductDescription.setText(product.getDescription());
                } else {
                    Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching product details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
