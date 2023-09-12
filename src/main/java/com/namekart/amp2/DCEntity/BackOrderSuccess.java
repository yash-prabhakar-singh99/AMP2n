package com.namekart.amp2.DCEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackOrderSuccess {
    String domain,type,dropDate;
    int amount,maxBid;

    public BackOrderSuccess() {
    }

    public BackOrderSuccess(String domain, String type, String dropDate, int amount, int maxBid) {
        this.domain = domain;
        this.type = type;
        this.dropDate = dropDate;
        this.amount = amount;
        this.maxBid = maxBid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        domain = domain;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        type = type;
    }


    public String getDropDate() {
        return dropDate;
    }


    public void setDropDate(String dropDate) {
        dropDate = dropDate;
    }


    public int getAmount() {
        return amount;
    }


    public void setAmount(int amount) {
        amount = amount;
    }


    public int getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(int maxBid) {
        maxBid = maxBid;
    }
}
