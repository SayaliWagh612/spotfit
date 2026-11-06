package com.eklavya.spotfit.model;

public class AnalyticsSummary {
    private String spaceId;
    private double totalRevenue;
    private int totalBookings;
    private double occupancyRate;
    private double customerSatisfaction;

    public AnalyticsSummary() {}

    public AnalyticsSummary(String spaceId, double totalRevenue, int totalBookings, double occupancyRate, double customerSatisfaction) {
        this.spaceId = spaceId;
        this.totalRevenue = totalRevenue;
        this.totalBookings = totalBookings;
        this.occupancyRate = occupancyRate;
        this.customerSatisfaction = customerSatisfaction;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public double getCustomerSatisfaction() {
        return customerSatisfaction;
    }

    public void setCustomerSatisfaction(double customerSatisfaction) {
        this.customerSatisfaction = customerSatisfaction;
    }

    
}

