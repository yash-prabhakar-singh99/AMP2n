package com.namekart.amp2.NamecheapEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaleBid {
    String saleId;
    Boolean isProxy, isLeadingBid;
    Float amount;

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    @JsonProperty("isProxy")
    public Boolean getProxy() {
        return isProxy;
    }

    @JsonProperty("isProxy")
    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    @JsonProperty(value="isLeadingBid")
    public Boolean getLeadingBid() {
        return isLeadingBid;
    }

    @JsonProperty(value="isLeadingBid")
    public void setLeadingBid(Boolean leadingBid) {
        isLeadingBid = leadingBid;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
