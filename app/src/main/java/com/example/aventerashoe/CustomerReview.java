package com.example.aventerashoe;

public class CustomerReview {
    private String customerName;
    private int rating;
    private String reviewText;
    private String productName;
    private int customerImageResId; // Ensure this is int

    public CustomerReview(String customerName, int rating, String reviewText, String productName, int customerImageResId) {
        this.customerName = customerName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.productName = productName;
        this.customerImageResId = customerImageResId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getProductName() {
        return productName;
    }

    public int getCustomerImageResId() {
        return customerImageResId; // Returns the drawable resource ID
    }
}

