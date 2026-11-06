package com.eklavya.spotfit.model;

public class FavoriteSpace {
    private String userId;
    private String spaceId;
    private long createdAt;

    public FavoriteSpace() {
    }

    public FavoriteSpace(String userId, String spaceId, long createdAt) {
        this.userId = userId;
        this.spaceId = spaceId;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
