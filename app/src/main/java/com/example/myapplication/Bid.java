package com.example.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Bid {
    private String bid_id;
    private String property_id;
    private String bidder_id;
    private String bidder_wallet;
    private String auctioneer_id;
    private String auctioneer_wallet;
    private String time_stamp;
    private Double bid_amount;
    private String bid_status;
    private String formattedTimeStamp; // Temporary field for formatted timestamp

    // No-argument constructor required for Firestore deserialization
    public Bid() {
    }

    public Bid(String bidId, String propertyID, String bidderId, String bidderWallet, String auctioneerId, String auctioneerWallet, Double bidAmount, String timeStamp, String bidStatus) {
        bid_id = bidId;
        property_id = propertyID;
        bidder_id = bidderId;
        bidder_wallet = bidderWallet;
        auctioneer_id = auctioneerId;
        auctioneer_wallet = auctioneerWallet;
        time_stamp = timeStamp;
        bid_amount = bidAmount;
        bid_status = bidStatus;
    }
    public String getBid_id() {
        return this.bid_id;
    }
    public String getPropertyID() {
        return this.property_id;
    }
    public String getBidder_id() {
        return this.bidder_id;
    }
    public void setBid_id(String bid_id) {
        this.bid_id = bid_id;
    }

    public String getBidder_wallet() {
        return this.bidder_wallet;
    }
    public String getAuctioneer_id(){
        return this.auctioneer_id;
    }
    public String getAuctioneer_wallet() {
        return this.auctioneer_wallet;
    }
    public double getBid_amount() {
        return bid_amount;
    }
    public String getTime_stamp() {
        return this.time_stamp;
    }
    public String getBid_status() {
        return this.bid_status;
    }

    // Getter and setter for formattedTimeStamp
    public String getFormattedTimeStamp() {
        return formattedTimeStamp;
    }

    public void setFormattedTimeStamp(String formattedTimeStamp) {
        this.formattedTimeStamp = formattedTimeStamp;
    }

    public void setBidStatus(String bid_status) {
        this.bid_status = bid_status;
    }
}