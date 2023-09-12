package com.namekart.amp2.DCEntity;

import javax.persistence.*;

@Entity
public class AuctionDetailDC {
    @Column(unique=true)
    Long auctionId;
    Long highBid, maxBid, numberOfBidders, minimumNextBid, bidIncrement;
    String name, endTime, highestBidder, type;
    Integer GDV;

    public Integer getEST() {
        return EST;
    }

    public void setEST(Integer EST) {
        this.EST = EST;
    }

    Integer EST;
    Boolean initialList;
    Boolean endList;

    public Boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    Boolean highlight;

    public Boolean getInitialList() {
        return initialList;
    }

    public void setInitialList(Boolean initialList) {
        this.initialList = initialList;
    }

    public Boolean getEndList() {
        return endList;
    }

    public void setEndList(Boolean endList) {
        this.endList = endList;
    }

    public Integer getGDV() {
        return GDV;
    }

    public void setGDV(Integer GDV) {
        this.GDV = GDV;
    }

    boolean winning;

    String addTime,timeLeft;
    boolean live;

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;


    public AuctionDetailDC() {
    }

    @Override
    public String toString() {
        return "AuctionDetailDC{" +
                "auctionId=" + auctionId +
                ", highBid=" + highBid +
                ", maxBid=" + maxBid +
                ", numberOfBidders=" + numberOfBidders +
                ", minimumNextBid=" + minimumNextBid +
                ", bidIncrement=" + bidIncrement +
                ", name='" + name + '\'' +
                ", endTime='" + endTime + '\'' +
                ", highestBidder='" + highestBidder + '\'' +
                ", type='" + type + '\'' +
                ", winning=" + winning +
                '}';
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

    public Long getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(Long maxBid) {
        this.maxBid = maxBid;
    }

    public Long getNumberOfBidders() {
        return numberOfBidders;
    }

    public void setNumberOfBidders(Long numberOfBidders) {
        this.numberOfBidders = numberOfBidders;
    }

    public Long getMinimumNextBid() {
        return minimumNextBid;
    }

    public void setMinimumNextBid(Long minimumNextBid) {
        this.minimumNextBid = minimumNextBid;
    }

    public Long getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(Long bidIncrement) {
        this.bidIncrement = bidIncrement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWinning() {
        return winning;
    }

    public void setWinning(boolean winning) {
        this.winning = winning;
    }
}
