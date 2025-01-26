package com.example.aventerashoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    private final Context context;
    private final List<Order> orderList;

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderDetails.setText("Name: " + order.getName() + "\n" +
                "Status: " + order.getStatus() + "\n" +
                "Product: " + order.getProductName() + "\n" +
                "Quantity: " + order.getQuantity() + "\n" +
                "Phone Number: " + order.getPhone() + "\n" +
                "Address: " + order.getAddress());

        // Handle Status Updates
        holder.btnShip.setOnClickListener(v -> updateOrderStatus(order, "Shipped"));
        holder.btnDeliver.setOnClickListener(v -> updateOrderStatus(order, "Delivered"));
        holder.btnCancel.setOnClickListener(v -> updateOrderStatus(order, "Cancelled"));
    }

    private void updateOrderStatus(Order order, String status) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(order.getOrderId());
        orderRef.child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Order status updated to " + status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDetails;
        Button btnShip, btnDeliver, btnCancel;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
            btnShip = itemView.findViewById(R.id.btn_ship);
            btnDeliver = itemView.findViewById(R.id.btn_deliver);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
