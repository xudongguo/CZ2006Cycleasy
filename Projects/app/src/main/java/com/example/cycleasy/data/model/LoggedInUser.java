package com.example.cycleasy.data.model;

/**
 * Data class that captures user information for logged in users retrieved from UserRepository
 */
public class LoggedInUser {

    private String userId, displayName, email;


    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() { return  email; }
}
