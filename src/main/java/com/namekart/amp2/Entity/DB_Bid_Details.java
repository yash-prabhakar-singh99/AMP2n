package com.namekart.amp2.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class DB_Bid_Details {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    String bidder_name,bid_price,currency,bid_status;
    long timestamp;
    boolean is_proxy_auto_bid;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="dbid",nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private DBdetails dbdetails;

    public DB_Bid_Details(String bidder_name, String bid_price, String currency, String bid_status, long timestamp, boolean is_proxy_auto_bid, DBdetails dbdetails) {
        this.bidder_name = bidder_name;
        this.bid_price = bid_price;
        this.currency = currency;
        this.bid_status = bid_status;
        this.timestamp = timestamp;
        this.is_proxy_auto_bid = is_proxy_auto_bid;
        this.dbdetails = dbdetails;
    }



    public DBdetails getDbdetails() {
        return dbdetails;
    }

    public void setDbdetails(DBdetails dbdetails) {
        this.dbdetails = dbdetails;
    }

    public DB_Bid_Details(DBdetails dbdetails, Bid_details bid_details) {
        this.dbdetails = dbdetails;
        this.bidder_name = bid_details.bidder_name;
        this.bid_price = bid_details.bid_price;
        this.currency = bid_details.currency;
        this.bid_status = bid_details.bid_status;
        this.timestamp = bid_details.timestamp;
        this.is_proxy_auto_bid = bid_details.is_proxy_auto_bid;
    }

    public Long getId() {
        return id;
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

    public DB_Bid_Details() {
    }


}
