package com.example.aventerashoe;

public class Order {
    private String orderId;
    private String name;
    private String productName;
    private String address;
    private int quantity; // Keep as int if stored as a number
    private String status;
    private String phone;
    private String email;
    private String productImageUrl;
    private double totalCost; // Use double for monetary values

    // Default constructor (required for Firebase)
    public Order() {}

    // Parameterized constructor
    public Order(String orderId, String name,String phone, String productName, String address, int quantity, String email, String productImageUrl, double totalCost, String status) {
        this.orderId = orderId;
        this.name = name;
        this.productName = productName;
        this.phone = phone;
        this.address = address;
        this.quantity = quantity;
        this.status = status;
        this.email = email;
        this.productImageUrl = productImageUrl;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
