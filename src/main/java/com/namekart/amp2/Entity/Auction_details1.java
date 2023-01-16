package com.namekart.amp2.Entity;

import java.util.List;

public class Auction_details1 {

    Auction_json1 auction_json;
    List<Bid_details> bid_history;

    public Auction_json1 getAuction_json() {
        return auction_json;
    }

    public void setAuction_json(Auction_json1 auction_json) {
        this.auction_json = auction_json;
    }

    public List<Bid_details> getBid_history() {
        return bid_history;
    }

    public void setBid_history(List<Bid_details> bid_history) {
        this.bid_history = bid_history;
    }

    public Auction_details1() {
    }
}

