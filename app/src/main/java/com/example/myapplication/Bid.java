package com.example.myapplication;

public class Bid {
    private final String bid_id;
    private final String property_id;
    private final String bidder_id;
    private final String bidder_wallet;
    private final String auctioneer_id;
    private final String auctioneer_wallet;
    private final String time_stamp;
    private final Double bid_amount;
    private String bid_status;


    public Bid(String bidId, String propertyId, String bidderId, String bidderWallet, String auctioneerId, String auctioneerWallet, Double bidAmount, String timeStamp, String bidStatus) {
        bid_id = bidId;
        property_id = propertyId;
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


    public void setBidStatus(String bid_status) {
        this.bid_status = bid_status;
    }
}