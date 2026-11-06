package com.eklavya.spotfit.model;

public class Complaint {
    private String complaintId;
    private String spaceId;
    private String bookingId;
    private String customerName;
    private String type;
    private String priority;
    private String subject;
    private String description;
    private String status;
    private String managerNotes;
    private String ownerNotes;
    private boolean escalatedToOwner;
    private String createdAt;
    private String resolvedAt;

    public Complaint() {}

    public Complaint(String complaintId, String spaceId, String bookingId, String customerName, String type,
                     String priority, String subject, String description, String status,
                     String managerNotes, String ownerNotes, boolean escalatedToOwner, String createdAt, String resolvedAt) {
        this.complaintId = complaintId;
        this.spaceId = spaceId;
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.type = type;
        this.priority = priority;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.managerNotes = managerNotes;
        this.ownerNotes = ownerNotes;
        this.escalatedToOwner = escalatedToOwner;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerNotes() {
        return managerNotes;
    }

    public void setManagerNotes(String managerNotes) {
        this.managerNotes = managerNotes;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }

    public boolean isEscalatedToOwner() {
        return escalatedToOwner;
    }

    public void setEscalatedToOwner(boolean escalatedToOwner) {
        this.escalatedToOwner = escalatedToOwner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

}

