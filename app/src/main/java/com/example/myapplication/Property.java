package com.example.myapplication;

import org.web3j.abi.datatypes.Int;

public class Property {
    private final int propertyId; // Changed to camelCase
    private final String eircode;
    private final String link;
    private final Integer auctioneer_id;
    private final Integer asking_price;
    private final Integer current_bid;

    // Constructor
    public Property(int propertyId, String eircode, String link, Integer auctioneer_id, Integer asking_price, Integer current_bid) {
        this.propertyId = propertyId; // Changed to camelCase
        this.eircode = eircode;
        this.link = link;
        this.auctioneer_id = auctioneer_id;
        this.asking_price= asking_price;
        this.current_bid = current_bid;
    }

    // Getter methods for propertyId, eircode, link
    public int getPropertyId() {
        return propertyId; // Changed to camelCase
    }

    public String getEircode() {
        return eircode;
    }

    public String getLink() {
        return link;
    }

    public Integer getAuctioneer_id() {
        return auctioneer_id;
    }
    public Integer getAsking_price() {
        return asking_price;
    }
    public Integer getCurrent_bid() {
        return current_bid;
    }
}
