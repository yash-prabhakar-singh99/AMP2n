package com.namekart.amp2.DCEntity;

public class BidPlacedFailure {
    Long auctionId;
    Errordc error;

    public BidPlacedFailure() {
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Errordc getError() {
        return error;
    }

    public void setError(Errordc error) {
        this.error = error;
    }
}
