package com.example.myapplication;

import com.google.firebase.firestore.IgnoreExtraProperties;

// User adapter to be used by activities interacting with the User table
@IgnoreExtraProperties
public class User {
    private String id;
    private String wallet_address;
    private String first_name;
    private String last_name;
    private String role;

    // No-argument constructor required for Firestore deserialization
    public User() {
    }

    public User(String id,String firstName, String lastName, String role, String wallet_address) {
        this.id = id;
        this.first_name = firstName;
        this.last_name = lastName;
        this.role = role;
        this.wallet_address = wallet_address;

    }
    // Getters and setters for each variable.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWalletAddress() {
        return wallet_address;
    }

    public void setWalletAddress(String wallet_address) {
        this.wallet_address = wallet_address;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
