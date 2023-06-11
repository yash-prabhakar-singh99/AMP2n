package com.namekart.amp2.GoDaddyEntities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Closeoutdb {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    String platform, domain,currPrice,endTime,endTimeist,timeLeft,ourPrice,gdv,auctype,bidders,age,est,status;

    Boolean watchlisted;

    public Boolean getWatchlisted() {
        return watchlisted;
    }

    public void setWatchlisted(Boolean watchlisted) {
        this.watchlisted = watchlisted;
    }

    public Closeoutdb(String platform, String domain, String currPrice, String endTime, String endTimeist, String timeLeft, String ourPrice, String gdv, String auctype, String status) {
        this.domain = domain;
        this.currPrice = currPrice;
        this.endTime = endTime;
        this.endTimeist = endTimeist;
        this.timeLeft = timeLeft;
        this.ourPrice = ourPrice;
        this.gdv = gdv;
        this.auctype = auctype;
        this.status = status;
        this.platform=platform;
    }

    public Closeoutdb() {
    }

    public Closeoutdb(String platform,String domain, String currPrice, String endTime, String endTimeist, String timeLeft, String gdv, String auctype, String status) {
        this.domain = domain;
        this.currPrice = currPrice;
        this.endTime = endTime;
        this.endTimeist = endTimeist;
        this.timeLeft = timeLeft;
        this.gdv = gdv;
        this.auctype = auctype;
        this.status = status;
        this.platform=platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCurrPrice() {
        return currPrice;
    }

    public void setCurrPrice(String currPrice) {
        this.currPrice = currPrice;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTimeist() {
        return endTimeist;
    }

    public void setEndTimeist(String endTimeist) {
        this.endTimeist = endTimeist;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getOurPrice() {
        return ourPrice;
    }

    public void setOurPrice(String ourPrice) {
        this.ourPrice = ourPrice;
    }

    public String getGdv() {
        return gdv;
    }

    public void setGdv(String gdv) {
        this.gdv = gdv;
    }

    public String getAuctype() {
        return auctype;
    }

    public void setAuctype(String auctype) {
        this.auctype = auctype;
    }

    public String getBidders() {
        return bidders;
    }

    public void setBidders(String bidders) {
        this.bidders = bidders;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEst() {
        return est;
    }

    public void setEst(String est) {
        this.est = est;
    }
}
