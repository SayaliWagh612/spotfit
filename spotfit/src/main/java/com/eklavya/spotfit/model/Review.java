package com.eklavya.spotfit.model;

public class Review {
    private String reviewId;
    private String spaceId;
    private String bookingId;
    private String customerName;
    private int rating;
    private String comment;
    private String date;
    private String status;
    private String managerReply;
    private String ownerReply;
    private String reportedReason;

    public Review() {}

    public Review(String reviewId, String spaceId, String bookingId, String customerName, int rating, String comment,
                  String date, String status, String managerReply, String ownerReply, String reportedReason) {
        this.reviewId = reviewId;
        this.spaceId = spaceId;
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.status = status;
        this.managerReply = managerReply;
        this.ownerReply = ownerReply;
        this.reportedReason = reportedReason;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerReply() {
        return managerReply;
    }

    public void setManagerReply(String managerReply) {
        this.managerReply = managerReply;
    }

    public String getOwnerReply() {
        return ownerReply;
    }

    public void setOwnerReply(String ownerReply) {
        this.ownerReply = ownerReply;
    }

    public String getReportedReason() {
        return reportedReason;
    }

    public void setReportedReason(String reportedReason) {
        this.reportedReason = reportedReason;
    }

    
    
}

