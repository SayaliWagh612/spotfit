package com.eklavya.spotfit.model;

public class SpaceType {
    private String spaceTypeId;
    private String activityTypeId;
    private String name;

    public SpaceType() {}

    public SpaceType(String spaceTypeId, String activityTypeId, String name) {
        this.spaceTypeId = spaceTypeId;
        this.activityTypeId = activityTypeId;
        this.name = name;
    }
    
    public String getSpaceTypeId() {
        return spaceTypeId;
    }

    public void setSpaceTypeId(String spaceTypeId) {
        this.spaceTypeId = spaceTypeId;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
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

