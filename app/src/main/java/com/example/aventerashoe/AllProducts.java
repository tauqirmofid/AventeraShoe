package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllProducts extends AppCompatActivity {

    private RecyclerView recyclerViewAllProducts;
    private AllProductsAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        // Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);




        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to homepage)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(AllProducts.this, Homepage.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });

        // Initialize RecyclerView
        recyclerViewAllProducts = findViewById(R.id.recyclerViewAllProducts);
        recyclerViewAllProducts.setLayoutManager(new GridLayoutManager(this, 2)); // 2 products per row

        // Initialize adapter
        adapter = new AllProductsAdapter(this, productList);
        recyclerViewAllProducts.setAdapter(adapter);

        // Fetch products from Firebase
        fetchProducts();
    }

    private void fetchProducts() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
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
                Toast.makeText(AllProducts.this, "Failed to load products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
