package com.example.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Bid {
    private String bid_id;
    private String propertyID;
    private String bidder_id;
    private String bidder_wallet;
    private String auctioneer_id;
    private String auctioneer_wallet;
    private String time_stamp;
    private Double bid_amount;
    private String bid_status;
    private String bid_description;
    private boolean contract_generated;
    // No-argument constructor required for Firestore deserialization
    public Bid() {
    }

    public Bid(String bidId, String property_id, String bidderId, String bidderWallet, String auctioneerId, String auctioneerWallet, Double bidAmount, String timeStamp, String bidStatus, boolean contractGenerated, String description) {
        bid_id = bidId;
        propertyID = property_id;
        bidder_id = bidderId;
        bidder_wallet = bidderWallet;
        auctioneer_id = auctioneerId;
        auctioneer_wallet = auctioneerWallet;
        time_stamp = timeStamp;
        bid_amount = bidAmount;
        bid_status = bidStatus;
        bid_description = description;
        contract_generated = contractGenerated;
    }
    public String getBid_id() {
        return this.bid_id;
    }
    public String getPropertyID() {
        return this.propertyID;
    }
    public String getBidder_id() {
        return this.bidder_id;
    }
    public void setBid_id(String bid_id) {
        this.bid_id = bid_id;
    }
    public void setTime_stamp(String time_stamp) {this.time_stamp = time_stamp;}
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
    public String getBid_description() {return this.bid_description;}
    public void setBid_description(String description) {this.bid_description = description;}
    public void setBidStatus(String bid_status) {
        this.bid_status = bid_status;
    }
    public void setContract_generated(boolean contract_generated) {
        this.contract_generated = contract_generated;
    }
    public boolean getContract_generated() {
        return this.contract_generated;
    }

}

