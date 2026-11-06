package com.eklavya.spotfit.model;

public class ActivityLog {
    private String logId;
    private String spaceId;
    private String actorRole;
    private String actorName;
    private String action;
    private String details;
    private String timestamp;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ActivityLog() {}

    public ActivityLog(String logId, String spaceId, String actorRole, String actorName, String action, String details, String timestamp) {
        this.logId = logId;
        this.spaceId = spaceId;
        this.actorRole = actorRole;
        this.actorName = actorName;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

}

