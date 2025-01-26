package com.example.aventerashoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.CustomerProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private OnItemClickListener listener;

    public CustomerProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }



    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    @NonNull
    @Override
    public CustomerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate customer-specific layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_customer, parent, false);
        return new CustomerProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: BDT" + product.getPrice());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.productImage);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CustomerProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public CustomerProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }



}

