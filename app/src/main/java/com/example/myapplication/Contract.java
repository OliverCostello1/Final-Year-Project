package com.example.myapplication;

public class Contract {
    private String bidId;
    private String contractAddress;
    private String transactionHash;
    private String createdAt;
    // Store as a formatted string

    public Contract() {
        // Empty constructor for Firestore
    }

    public Contract(String bidId, String contractAddress, String transactionHash, String createdAt) {
        this.bidId = bidId;
        this.contractAddress = contractAddress;
        this.transactionHash = transactionHash;
        this.createdAt = createdAt;

    }

    public String getBidId() {
        return bidId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
