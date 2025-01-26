package com.example.aventerashoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class CustomerReviewAdapter extends RecyclerView.Adapter<CustomerReviewAdapter.ReviewViewHolder> {

    private List<CustomerReview> customerReviewList;

    public CustomerReviewAdapter(List<CustomerReview> customerReviewList) {
        this.customerReviewList = customerReviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        CustomerReview review = customerReviewList.get(position);
        holder.customerName.setText(review.getCustomerName());
        holder.reviewText.setText(review.getReviewText());
        holder.productName.setText("Product: " + review.getProductName());
        // For stars, you can customize here if needed
        holder.customerImage.setImageResource(review.getCustomerImageResId()); // This assumes you use drawable IDs
    }

    @Override
    public int getItemCount() {
        return customerReviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, reviewText, productName;
        ImageView customerImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            customerImage = itemView.findViewById(R.id.customerImage);
            customerName = itemView.findViewById(R.id.customerName);
            reviewText = itemView.findViewById(R.id.reviewText);
            productName = itemView.findViewById(R.id.productName);
        }
    }
}

