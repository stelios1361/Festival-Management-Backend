package com.festivalmanager.dto;

public class UpdateAccountStatusRequest {
    private String requesterUsername;  // who is executing the request
    private String token;              // their token
    private String targetUsername;     // the user whose account status is being changed
    private Boolean newActive;         // true = activate, false = deactivate

    // getters and setters

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public Boolean getNewActive() {
        return newActive;
    }

    public void setNewActive(Boolean newActive) {
        this.newActive = newActive;
    }
}
