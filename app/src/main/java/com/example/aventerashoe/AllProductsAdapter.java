package com.example.aventerashoe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public AllProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allproduct_customer, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product data
        holder.productName.setText(product.getName());
        holder.productPrice.setText("BDT " + product.getPrice());
        Glide.with(context).load(product.getImageUrl()).into(holder.productImage);

        // Handle item click
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductPageActivity.class);
            intent.putExtra("productId", product.getProductId()); // Pass product ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView productName, productPrice;
        CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            cardView = itemView.findViewById(R.id.product); // Root view of the product
        }
    }
}
