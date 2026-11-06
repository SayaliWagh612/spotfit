package com.eklavya.spotfit.model;

public class Customer {
    private String customerId;
    private String name;
    private String email;
    private String phone;
    private int totalBookingsCount;
    private double totalSpent;

    public Customer() {}

    public Customer(String customerId, String name, String email, String phone, int totalBookingsCount, double totalSpent) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.totalBookingsCount = totalBookingsCount;
        this.totalSpent = totalSpent;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTotalBookingsCount() {
        return totalBookingsCount;
    }

    public void setTotalBookingsCount(int totalBookingsCount) {
        this.totalBookingsCount = totalBookingsCount;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    
}

