package com.namekart.amp2.Entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)

public class ClosedAuctionsDets {


    String Domain, AuctionId, AuctionStatusId, AuctionStatus, BidPrice, BidPriceCurrency,AuctionWonStatus,YourHighBid,YourProxyBid;

    public ClosedAuctionsDets() {
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getAuctionId() {
        return AuctionId;
    }

    public void setAuctionId(String auctionId) {
        AuctionId = auctionId;
    }

    public String getAuctionStatusId() {
        return AuctionStatusId;
    }

    public void setAuctionStatusId(String auctionStatusId) {
        AuctionStatusId = auctionStatusId;
    }

    public String getAuctionStatus() {
        return AuctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        AuctionStatus = auctionStatus;
    }

    public String getBidPrice() {
        return BidPrice;
    }

    public void setBidPrice(String bidPrice) {
        BidPrice = bidPrice;
    }

    public String getBidPriceCurrency() {
        return BidPriceCurrency;
    }

    public void setBidPriceCurrency(String bidPriceCurrency) {
        BidPriceCurrency = bidPriceCurrency;
    }

    public String getAuctionWonStatus() {
        return AuctionWonStatus;
    }

    public void setAuctionWonStatus(String auctionWonStatus) {
        AuctionWonStatus = auctionWonStatus;
    }

    public String getYourHighBid() {
        return YourHighBid;
    }

    public void setYourHighBid(String yourHighBid) {
        YourHighBid = yourHighBid;
    }

    public String getYourProxyBid() {
        return YourProxyBid;
    }

    public void setYourProxyBid(String yourProxyBid) {
        YourProxyBid = yourProxyBid;
    }
}
