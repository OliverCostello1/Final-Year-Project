package com.example.myapplication;

public class User {
    private final String wallet_address;
    private final String first_name;
    private final String last_name;
    private final String role;
    private final String id;
    public User(String id, String walletAddress, String firstName, String lastName, String role) {
        this.id = id;
        this.wallet_address = walletAddress;
        this.first_name = firstName;
        this.last_name = lastName;
        this.role = role;
    }

    public String getID() {
        return id;
    }

    // Getter methods for user table
    public String getWalletAddress() {
        return wallet_address;
    }
    public String getFirstName() {
        return first_name;
    }
    public String getLastName() {
        return last_name;
    }
    public String getRole() {
        return role;
    }

    public void setId(int id) {
    }
}
