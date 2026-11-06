package com.eklavya.spotfit.model;

public class ActivityType {
    private String activityTypeId;
    private String category;
    private String name;

    public ActivityType() {}

    public ActivityType(String activityTypeId, String category, String name) {
        this.activityTypeId = activityTypeId;
        this.category = category;
        this.name = name;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return name; }
}

