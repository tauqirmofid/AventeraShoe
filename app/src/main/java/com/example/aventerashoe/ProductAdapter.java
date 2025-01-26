package com.example.aventerashoe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnProductClickListener listener;

    // Interface for handling item clicks
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvProductStock;
        private final Button btnViewDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }

        public void bind(Product product, OnProductClickListener listener) {
            // Set product details
            tvProductName.setText(product.getName());
            tvProductPrice.setText("Price: BDT" + product.getPrice());
            tvProductStock.setText("Stock: " + product.getStock());

            // Load product image using Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                  //  .placeholder(R.drawable.placeholder_image) // Optional placeholder
                 //   .error(R.drawable.error_image) // Optional error image
                    .into(ivProductImage);

            // Handle item click
            itemView.setOnClickListener(v -> listener.onProductClick(product));

            // Handle View Details button click
            btnViewDetails.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId", product.getProductId());
                context.startActivity(intent);
            });
        }
    }
}
