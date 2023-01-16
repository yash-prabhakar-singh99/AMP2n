package com.namekart.amp2.DCEntity;

public class BidPlacedSuccess {
    String bidResult, highBidder,endTime;
    Long auctionId, highBid,nextValidBid,maxBid,numberOfBids;
    boolean winning;

    public BidPlacedSuccess() {
    }

    public String getBidResult() {
        return bidResult;
    }

    public void setBidResult(String bidResult) {
        this.bidResult = bidResult;
    }

    public String getHighBidder() {
        return highBidder;
    }

    public void setHighBidder(String highBidder) {
        this.highBidder = highBidder;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getHighBid() {
        return highBid;
    }

    public void setHighBid(Long highBid) {
        this.highBid = highBid;
    }

    public Long getNextValidBid() {
        return nextValidBid;
    }

    public void setNextValidBid(Long nextValidBid) {
        this.nextValidBid = nextValidBid;
    }

    public Long getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(Long maxBid) {
        this.maxBid = maxBid;
    }

    public Long getNumberOfBids() {
        return numberOfBids;
    }

    public void setNumberOfBids(Long numberOfBids) {
        this.numberOfBids = numberOfBids;
    }

    public boolean isWinning() {
        return winning;
    }

    public void setWinning(boolean winning) {
        this.winning = winning;
    }
}
