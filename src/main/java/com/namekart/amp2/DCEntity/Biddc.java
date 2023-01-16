package com.namekart.amp2.DCEntity;

public class Biddc {
    Long auctionId,amount;

    public Biddc() {
    }

    public Biddc(Long auctionId, Long amount) {
        this.auctionId = auctionId;
        this.amount = amount;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
