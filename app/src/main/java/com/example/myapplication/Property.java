package com.example.myapplication;

import org.web3j.abi.datatypes.Int;

public class Property {
    private final int propertyId;
    private final String eircode;
    private final String link;
    private final Integer auctioneer_id;
    private final double asking_price;
    private final Integer current_bid;
    private final String auctioneer_wallet;
    // Constructor
    public Property(int propertyId, String eircode, String link, Integer auctioneer_id, double asking_price, Integer current_bid, String auctioneerWallet) {
        this.propertyId = propertyId;
        this.eircode = eircode;
        this.link = link;
        this.auctioneer_id = auctioneer_id;
        this.asking_price= asking_price;
        this.current_bid = current_bid;
        this.auctioneer_wallet = auctioneerWallet;
    }

    // Getter methods for propertyId, eircode, link
    public int getPropertyId() {
        return propertyId; }

    public String getEircode() {
        return eircode;
    }

    public String getLink() {
        return link;
    }

    public Integer getAuctioneer_id() {
        return auctioneer_id;
    }
    public double getAsking_price() {
        return asking_price;
    }
    public Integer getCurrent_bid() {
        return current_bid;
    }
    public String getAuctioneer_wallet() {
        return auctioneer_wallet;
    }
}