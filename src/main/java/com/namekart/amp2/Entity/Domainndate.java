package com.namekart.amp2.Entity;

import java.util.Date;

public class Domainndate {
    Date date;
    String pst,ist;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPst() {
        return pst;
    }

    public void setPst(String pst) {
        this.pst = pst;
    }

    public String getIst() {
        return ist;
    }

    public void setIst(String ist) {
        this.ist = ist;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public Domainndate(Date date, String pst, String ist, String domain, String bid) {
        this.date = date;
        this.pst = pst;
        this.ist = ist;
        this.domain = domain;
        this.bid = bid;
    }

    String domain;
    String bid;

    public Domainndate() {
    }

}
