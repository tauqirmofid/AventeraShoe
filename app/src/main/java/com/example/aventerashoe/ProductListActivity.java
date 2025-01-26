package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView rvProductList;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProductList = findViewById(R.id.rv_product_list);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList, this::onProductClick);
        rvProductList.setAdapter(adapter);


// Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);


        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to admin page)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ProductListActivity.this, AdminActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });


        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Load products from the database
        loadProducts();
    }

    private void loadProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProductListActivity.this, "Failed to load products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(ProductListActivity.this, EditProductActivity.class);
        intent.putExtra("productId", product.getProductId());
        startActivity(intent);
    }
}
