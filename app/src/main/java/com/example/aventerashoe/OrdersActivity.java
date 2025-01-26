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

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersDatabaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);



// Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);



        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to admin page)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(OrdersActivity.this, AdminActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });

        // Initialize RecyclerView
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Order List and Adapter
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        recyclerViewOrders.setAdapter(ordersAdapter);

        // Load Orders from Firebase
        ordersDatabaseRef = FirebaseDatabase.getInstance().getReference("Orders");
        loadOrders();
    }


    private void loadOrders() {
        ordersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(snapshot.getKey()); // Ensure the key is used as the orderId

                        orderList.add(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrdersActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
