package com.example.myapplication;

public class Property {
    private String propertyId;
    private String eircode;
    private String link;
    private String auctioneer_id;
    private double asking_price;
    private double current_bid;
    private String auctioneer_wallet;

    // Default constructor required for Firebase
    public Property() {}

    public Property(String propertyId, String eircode, String link, String auctioneerId, double askingPrice, double currentBid, String auctioneerWallet) {
        this.propertyId = propertyId;
        this.eircode = eircode;
        this.link = link;
        this.auctioneer_id = auctioneerId;
        this.asking_price = askingPrice;
        this.current_bid = currentBid;
        this.auctioneer_wallet = auctioneerWallet;
    }

    // Getters and setters if needed


    // Getter methods for propertyId, eircode, link
    public String getPropertyId() {
        return propertyId; }

    public String getEircode() {
        return eircode;
    }

    public String getLink() {
        return link;
    }

    public String getAuctioneer_id() {
        return auctioneer_id;
    }
    public double getAsking_price() {
        return asking_price;
    }
    public Double getCurrent_bid() {
        return current_bid;
    }
    public String getAuctioneer_wallet() {
        return auctioneer_wallet;
    }
}