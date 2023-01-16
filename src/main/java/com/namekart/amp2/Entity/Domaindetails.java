package com.namekart.amp2.Entity;

public class Domaindetails {
    String domainname;
    String bid;

    public Domaindetails() {
    }

    public Domaindetails(String domainname, String bid) {
        this.domainname = domainname;
        this.bid = bid;
    }

    public String getDomainname() {
        return domainname;
    }

    public void setDomainname(String domainname) {
        this.domainname = domainname;
    }

    public String getBid() {
        return bid;
    }
    public void setBid(String bid) {
        this.bid = bid;
    }
}
