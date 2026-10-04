package com.seep.model;

/**
 * Admin extends User -> demonstrates Inheritance.
 * Overrides getRole() -> demonstrates Polymorphism (method overriding).
 */
public class Admin extends User {

    private String designation;

    public Admin() {
        super();
        this.setRole("ADMIN");
        this.designation = "Emergency Coordinator";
    }

    public Admin(int userId, String username, String password, String fullName) {
        super(userId, username, password, fullName, "ADMIN");
        this.designation = "Emergency Coordinator";
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String toString() {
        return getFullName() + " (" + getUsername() + ") - " + designation;
    }
}
