package com.namekart.amp2.NamecheapEntity;

public class NCEventData {
    NCEventDataSale sale;
    NCNextBid nextBid;
    NCPrevBid previousBid;

    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public NCEventDataSale getSale() {
        return sale;
    }

    public void setSale(NCEventDataSale sale) {
        this.sale = sale;
    }

    public NCNextBid getNextBid() {
        return nextBid;
    }

    public void setNextBid(NCNextBid nextBid) {
        this.nextBid = nextBid;
    }

    public NCPrevBid getPreviousBid() {
        return previousBid;
    }

    public void setPreviousBid(NCPrevBid previousBid) {
        this.previousBid = previousBid;
    }
}
