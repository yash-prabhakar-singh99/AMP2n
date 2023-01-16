package com.namekart.amp2.NamecheapEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Livencdb {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer idd;
    String id;
    String addtime;

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    String time_left;

    Boolean live;

    public Integer getIdd() {
        return idd;
    }

    public void setIdd(Integer idd) {
        this.idd = idd;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    String status;
    String startDate;
    String endDate;
    String createdDate;
    String updatedDate;
    String name;
    String keywordSearchQuery;

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    String auctionType;

    Float estibotValue, minBid, price, startPrice, renewPrice, soldprice;

    Integer bidCount, ahrefsDomainRating, alexaRanking, backlinksCount, extensionsTaken, keywordSearchCount, monthlyVisitors, pageRank, soldYear, umbrellaRanking;

    public Livencdb() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeywordSearchQuery() {
        return keywordSearchQuery;
    }

    public void setKeywordSearchQuery(String keywordSearchQuery) {
        this.keywordSearchQuery = keywordSearchQuery;
    }

    public Float getEstibotValue() {
        return estibotValue;
    }

    public void setEstibotValue(Float estibotValue) {
        this.estibotValue = estibotValue;
    }

    public Float getMinBid() {
        return minBid;
    }

    public void setMinBid(Float minBid) {
        this.minBid = minBid;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Float startPrice) {
        this.startPrice = startPrice;
    }

    public Float getRenewPrice() {
        return renewPrice;
    }

    public void setRenewPrice(Float renewPrice) {
        this.renewPrice = renewPrice;
    }

    public Float getSoldprice() {
        return soldprice;
    }

    public void setSoldprice(Float soldprice) {
        this.soldprice = soldprice;
    }

    public Integer getBidCount() {
        return bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }

    public Integer getAhrefsDomainRating() {
        return ahrefsDomainRating;
    }

    public void setAhrefsDomainRating(Integer ahrefsDomainRating) {
        this.ahrefsDomainRating = ahrefsDomainRating;
    }

    public Integer getAlexaRanking() {
        return alexaRanking;
    }

    public void setAlexaRanking(Integer alexaRanking) {
        this.alexaRanking = alexaRanking;
    }

    public Integer getBacklinksCount() {
        return backlinksCount;
    }

    public void setBacklinksCount(Integer backlinksCount) {
        this.backlinksCount = backlinksCount;
    }

    public Integer getExtensionsTaken() {
        return extensionsTaken;
    }

    public void setExtensionsTaken(Integer extensionsTaken) {
        this.extensionsTaken = extensionsTaken;
    }

    public Integer getKeywordSearchCount() {
        return keywordSearchCount;
    }

    public void setKeywordSearchCount(Integer keywordSearchCount) {
        this.keywordSearchCount = keywordSearchCount;
    }

    public Integer getMonthlyVisitors() {
        return monthlyVisitors;
    }

    public void setMonthlyVisitors(Integer monthlyVisitors) {
        this.monthlyVisitors = monthlyVisitors;
    }

    public Integer getPageRank() {
        return pageRank;
    }

    public void setPageRank(Integer pageRank) {
        this.pageRank = pageRank;
    }

    public Integer getSoldYear() {
        return soldYear;
    }

    public void setSoldYear(Integer soldYear) {
        this.soldYear = soldYear;
    }

    public Integer getUmbrellaRanking() {
        return umbrellaRanking;
    }

    public void setUmbrellaRanking(Integer umbrellaRanking) {
        this.umbrellaRanking = umbrellaRanking;
    }

}
