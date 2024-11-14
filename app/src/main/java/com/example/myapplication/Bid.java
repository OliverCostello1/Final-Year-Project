package com.example.myapplication;

import org.web3j.abi.datatypes.Int;

public class Bid {
    private final int property_id;
    private final int bid_id;
    private final String bidder_wallet;
    private final int auctioneer_id;
    private final String auctioneer_wallet;
    private final String time_stamp;
    private final double bid_amount;
    private final String bid_status;


    public Bid(int propertyId, int bidId, String bidderWallet, int auctioneerId, String auctioneerWallet, String timeStamp, double bidAmount, String bidStatus) {
        property_id = propertyId;
        bid_id = bidId;
        bidder_wallet = bidderWallet;
        auctioneer_id = auctioneerId;
        auctioneer_wallet = auctioneerWallet;
        time_stamp = timeStamp;
        bid_amount = bidAmount;
        bid_status = bidStatus;
    }
    public int getPropertyID() {
        return this.property_id;
    }
    public int getBid_id() {
        return this.bid_id;
    }
    public String getBidder_wallet() {
        return this.bidder_wallet;
    }
    public int getAuctioneer_id(){
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
}
