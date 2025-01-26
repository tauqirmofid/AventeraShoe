package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminProductsActivity extends AppCompatActivity {
    private ListView lvProducts;
    private List<Product> productList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<String> productNames = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);

        lvProducts = findViewById(R.id.lv_products);
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        lvProducts.setAdapter(adapter);

        loadProducts();

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productList.get(position);
            Intent intent = new Intent(AdminProductsActivity.this, EditProductActivity.class);
            intent.putExtra("productId", selectedProduct.getProductId());
            startActivity(intent);
        });
    }

    private void loadProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                productNames.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                        productNames.add(product.getName());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminProductsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
