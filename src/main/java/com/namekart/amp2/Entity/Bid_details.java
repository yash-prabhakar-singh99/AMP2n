package com.namekart.amp2.Entity;

public class Bid_details {
    String bidder_name,bid_price,currency,bid_status;
    long timestamp;
    boolean is_proxy_auto_bid;

    public Bid_details() {
    }

    public String getBidder_name() {
        return bidder_name;
    }

    public void setBidder_name(String bidder_name) {
        this.bidder_name = bidder_name;
    }

    public String getBid_price() {
        return bid_price;
    }

    public void setBid_price(String bid_price) {
        this.bid_price = bid_price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBid_status() {
        return bid_status;
    }

    public void setBid_status(String bid_status) {
        this.bid_status = bid_status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isIs_proxy_auto_bid() {
        return is_proxy_auto_bid;
    }

    public void setIs_proxy_auto_bid(boolean is_proxy_auto_bid) {
        this.is_proxy_auto_bid = is_proxy_auto_bid;
    }
}
