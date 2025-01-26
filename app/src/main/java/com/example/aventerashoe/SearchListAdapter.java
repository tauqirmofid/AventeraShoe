package com.example.aventerashoe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder> {

    private final List<Product> searchResults;
    private final OnSearchItemClickListener listener;

    // Interface for handling clicks on search results
    public interface OnSearchItemClickListener {
        void onSearchItemClick(Product product);
    }

    public SearchListAdapter(List<Product> searchResults, OnSearchItemClickListener listener) {
        this.searchResults = searchResults;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Product product = searchResults.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProductImage;
        private final TextView tvProductName;
        private final TextView tvProductPrice;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void bind(Product product, OnSearchItemClickListener listener) {
            // Bind product details to views
            tvProductName.setText(product.getName());
            tvProductPrice.setText( product.getPrice());

            // Load product image using Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(ivProductImage);

            // Handle item click
            itemView.setOnClickListener(v -> listener.onSearchItemClick(product));
        }
    }
}
