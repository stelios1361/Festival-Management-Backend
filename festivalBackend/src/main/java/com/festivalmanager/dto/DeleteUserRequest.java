package com.festivalmanager.dto;

public class DeleteUserRequest {
    private String requesterUsername; // who is making the request
    private String token;             // their token
    private String targetUsername;    // the user to delete 
    
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
    
}
