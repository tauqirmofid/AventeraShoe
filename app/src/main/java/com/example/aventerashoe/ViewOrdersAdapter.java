package com.example.aventerashoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ViewOrdersAdapter extends RecyclerView.Adapter<ViewOrdersAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final Context context;

    public ViewOrdersAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vieworder, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);

        // Set up long-press listener
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteOrderDialog(order, position); // Trigger delete dialog on long press
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void showDeleteOrderDialog(Order order, int position) {
        if (!"Pending".equals(order.getStatus())) {
            Toast.makeText(context, "Only pending orders can be deleted.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteOrder(order, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteOrder(Order order, int position) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(order.getOrderId());

        orderRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                orders.remove(position); // Remove from local list
                notifyItemRemoved(position); // Notify adapter
                Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete order: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivProductImage;
        private final TextView tvProductName;
        private final TextView tvQuantity;
        private final TextView tvTotalCost;
        private final TextView tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalCost = itemView.findViewById(R.id.tvTotalCost);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Order order) {
            tvProductName.setText(order.getProductName());
            tvQuantity.setText("Quantity: " + order.getQuantity());
            tvTotalCost.setText("Total Cost: BDT" + order.getTotalCost());
            tvStatus.setText(order.getStatus());

            Glide.with(itemView.getContext())
                    .load(order.getProductImageUrl())
                    //.placeholder(R.drawable.placeholder_image)
                    .into(ivProductImage);
        }
    }
}
