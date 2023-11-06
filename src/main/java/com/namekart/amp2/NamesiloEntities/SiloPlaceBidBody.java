package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlElement;

public class SiloPlaceBidBody
{
    Long auctionId;

    Long userId;

    Float bid;

    Float proxyBid;

    Integer code;

    String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Float getBid() {
        return bid;
    }

    public void setBid(Float bid) {
        this.bid = bid;
    }

    public Float getProxyBid() {
        return proxyBid;
    }

    public void setProxyBid(Float proxyBid) {
        this.proxyBid = proxyBid;
    }
}
