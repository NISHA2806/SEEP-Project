package com.seep.model;

/**
 * Base class representing a registered citizen user of the system.
 * Demonstrates Encapsulation (private fields with getters/setters).
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role; // "USER" or "ADMIN"

    public User() {
        this.role = "USER";
    }

    public User(int userId, String username, String password, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Overridable -> supports Polymorphism when Admin overrides it
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ") [" + getRole() + "]";
    }
}
