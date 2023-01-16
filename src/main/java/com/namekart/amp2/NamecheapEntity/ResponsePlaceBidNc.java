package com.namekart.amp2.NamecheapEntity;

public class ResponsePlaceBidNc {
    String id, status, createdDate;
    Float maxAmount, amount, index;
    Boolean isLeadingBid;

    public ResponsePlaceBidNc() {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getIndex() {
        return index;
    }

    public void setIndex(Float index) {
        this.index = index;
    }

    public Boolean getLeadingBid() {
        return isLeadingBid;
    }

    public void setLeadingBid(Boolean leadingBid) {
        isLeadingBid = leadingBid;
    }
}
