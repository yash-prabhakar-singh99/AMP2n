package com.namekart.amp2.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dbdetails")
public class DBdetails {



    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    String domain;
    String wonby;
    String wonat;

    Boolean fetched;

    public Boolean getFetched() {
        return fetched;
    }

    public void setFetched(Boolean fetched) {
        this.fetched = fetched;
    }

    String namecheapid;

    public String getNamecheapid() {
        return namecheapid;
    }

    public void setNamecheapid(String namecheapid) {
        this.namecheapid = namecheapid;
    }

    Long auctionId;
    String mymaxbid;

    String platform;

    String currbid;

    Long bidders;

    String time_left;

    Long age;

    String estibot;

    String auctiontype;

    boolean watchlist;

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

    @OneToMany(mappedBy= "dbdetails", fetch= FetchType.LAZY, cascade= CascadeType.ALL)
     List<DB_Bid_Details> bidhistory;

    public DBdetails(String domain, String platform, String currbid, Long bidders, String time_left, Long age, String estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left = time_left;
        this.age = age;
        this.estibot = estibot;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.bidhistory= new ArrayList<>();
        this.watchlist=false;
    }


    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced, String namecheapid) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        this.namecheapid=namecheapid;
    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getMymaxbid() {
        return mymaxbid;
    }

    public void setMymaxbid(String mymaxbid) {
        this.mymaxbid = mymaxbid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCurrbid() {
        return currbid;
    }

    public void setCurrbid(String currbid) {
        this.currbid = currbid;
    }

    public Long getBidders() {
        return bidders;
    }

    public void setBidders(Long bidders) {
        this.bidders = bidders;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getEstibot() {
        return estibot;
    }

    public void setEstibot(String estibot) {
        this.estibot = estibot;
    }

    public String getAuctiontype() {
        return auctiontype;
    }

    public void setAuctiontype(String auctiontype) {
        this.auctiontype = auctiontype;
    }

    public List<DB_Bid_Details> getBidhistory() {
        return bidhistory;
    }

    public void setBidhistory(List<DB_Bid_Details> bidhistory) {
        this.bidhistory = bidhistory;
    }

    public String getWonby() {
        return wonby;
    }

    public void setWonby(String wonby) {
        this.wonby = wonby;
    }

    public String getWonat() {
        return wonat;
    }

    public void setWonat(String wonat) {
        this.wonat = wonat;
    }

    String bidAmount;
    String result;
    String endTimepst;
    String endTimeist;

    String bidplacetime;

    boolean isBidPlaced;

    public DBdetails() {
    }



    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }



    public String getEndTimepst() {
        return endTimepst;
    }

    public void setEndTimepst(String endTimepst) {
        this.endTimepst = endTimepst;
    }

    public String getEndTimeist() {
        return endTimeist;
    }

    public void setEndTimeist(String endTimeist) {
        this.endTimeist = endTimeist;
    }

    public String getBidplacetime() {
        return bidplacetime;
    }

    public void setBidplacetime(String bidplacetime) {
        this.bidplacetime = bidplacetime;
    }

    public boolean isBidPlaced() {
        return isBidPlaced;
    }

    public void setIsBidPlaced(boolean bidPlaced) {
        isBidPlaced = bidPlaced;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
