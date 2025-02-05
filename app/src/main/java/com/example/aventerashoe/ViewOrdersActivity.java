package com.example.aventerashoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private ViewOrdersAdapter ViewOrdersAdapter;
    private List<Order> orderList;
    private FirebaseAuth auth;
    private DatabaseReference ordersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);




        // Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);


        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to homepage)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ViewOrdersActivity.this, Homepage.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });



        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        ViewOrdersAdapter = new ViewOrdersAdapter(this, orderList); // Pass `this` as context
        recyclerViewOrders.setAdapter(ViewOrdersAdapter);

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadUserOrders();




    }





    private void loadUserOrders() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You need to be logged in to view your orders.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userEmail = auth.getCurrentUser().getEmail();

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null && order.getEmail().equals(userEmail)) {
                        orderList.add(order);
                    }
                }

                if (orderList.isEmpty()) {
                    recyclerViewOrders.setVisibility(View.GONE);
                    findViewById(R.id.emptyStateLayout).setVisibility(View.VISIBLE);
                } else {
                    recyclerViewOrders.setVisibility(View.VISIBLE);
                    findViewById(R.id.emptyStateLayout).setVisibility(View.GONE);
                }

                Collections.reverse(orderList);
                ViewOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewOrdersActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
