package com.namekart.amp2.Entity;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    Float myLastBid;

    public Float getMyLastBid() {
        return myLastBid;
    }

    public void setMyLastBid(Float myLastBid) {
        this.myLastBid = myLastBid;
    }

    public void setBidPlaced(boolean bidPlaced) {
        isBidPlaced = bidPlaced;
    }

    public Boolean getWasWatchlisted() {
        return wasWatchlisted;
    }

    public void setWasWatchlisted(Boolean wasWatchlisted) {
        this.wasWatchlisted = wasWatchlisted;
    }

    Boolean wasWatchlisted;
    Integer gdv;
    public Integer getGdv()
    {
        return gdv;
    }
    public void setGdv(Integer gdv) {
        this.gdv = gdv;
    }
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

    boolean track;

    public boolean isTrack() {
        return track;
    }

    public void setTrack(boolean track) {
        this.track = track;
    }

    String currbid;

    Long bidders;

    String time_left;

    Long age;

    Integer bids;

    public Integer getBids() {
        return bids;
    }

    public void setBids(Integer bids) {
        this.bids = bids;
    }

    Integer estibot;

    String auctiontype;

    boolean watchlist;

    int nw;

    public int getNw() {
        return nw;
    }

    public void setNw(int nw) {
        this.nw = nw;
    }

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

//    @OneToMany(mappedBy= "dbdetails", fetch= FetchType.LAZY, cascade= CascadeType.ALL)
//     List<DB_Bid_Details> bidhistory;

    public DBdetails(String domain, Integer gdv, Long auctionId, String platform, String currbid, String time_left, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, boolean isBidPlaced) {
        this.domain = domain;
        this.gdv = gdv;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.time_left = time_left;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.isBidPlaced = isBidPlaced;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
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
        //this.bidhistory= new ArrayList<>();
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId=auctionId;
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
        this.isBidPlaced = isBidPlaced;
        ft = new SimpleDateFormat("yyyy-MM-dd");

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
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String time_left,String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left=time_left;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, String bidAmount,Long bidders, String time_left,String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidAmount=bidAmount;
        this.bidders = bidders;
        this.time_left=time_left;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

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

    public Integer getEstibot() {
        return estibot;
    }

    public void setEstibot(Integer estibot) {
        this.estibot = estibot;
    }

    public String getAuctiontype() {
        return auctiontype;
    }

    public void setAuctiontype(String auctiontype) {
        this.auctiontype = auctiontype;
    }

    /*public List<DB_Bid_Details> getBidhistory() {
        return bidhistory;
    }*/

   /* public void setBidhistory(List<DB_Bid_Details> bidhistory) {
        this.bidhistory = bidhistory;
    }*/

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

    boolean estFlag;

    Integer keyword_exact_lsv;

    Float keyword_exact_cpc;

    String whois_create_date, whois_registrar;

    Integer end_users_buyers;

    Integer wayback_age;

    @Transient
    SimpleDateFormat ft= new SimpleDateFormat("yyyy-MM-dd");

    public void setAgeWhois(String whois_create_date)
    {
        try {
            Date d = ft.parse(whois_create_date);
            Date now = new Date();
            this.age= Long.valueOf(now.getYear()-d.getYear());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean isEstFlag() {
        return estFlag;
    }

    public void setEstFlag(boolean estFlag) {
        this.estFlag = estFlag;
    }

    public Integer getKeyword_exact_lsv() {
        return keyword_exact_lsv;
    }

    public void setKeyword_exact_lsv(Integer keyword_exact_lsv) {
        this.keyword_exact_lsv = keyword_exact_lsv;
    }

    public Float getKeyword_exact_cpc() {
        return keyword_exact_cpc;
    }

    public void setKeyword_exact_cpc(Float keyword_exact_cpc) {
        this.keyword_exact_cpc = keyword_exact_cpc;
    }

    public String getWhois_create_date() {
        return whois_create_date;
    }

    public void setWhois_create_date(String whois_create_date) {
        this.whois_create_date = whois_create_date;
    }

    public String getWhois_registrar() {
        return whois_registrar;
    }

    public void setWhois_registrar(String whois_registrar) {
        this.whois_registrar = whois_registrar;
    }

    public Integer getEnd_users_buyers() {
        return end_users_buyers;
    }

    public void setEnd_users_buyers(Integer end_users_buyers) {
        this.end_users_buyers = end_users_buyers;
    }

    public Integer getWayback_age() {
        return wayback_age;
    }

    public void setWayback_age(Integer wayback_age) {
        this.wayback_age = wayback_age;
    }

    public DBdetails() {
        ft = new SimpleDateFormat("yyyy-MM-dd");
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
