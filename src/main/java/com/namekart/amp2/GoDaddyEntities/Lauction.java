package com.namekart.amp2.GoDaddyEntities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class Lauction {
    @XmlAttribute(name = "ID")
    private String ID;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idd;
    @XmlAttribute(name = "Name")
    private String Name;

    @XmlAttribute(name = "Traffic")
    private String Traffic;

    @XmlAttribute(name = "BidCount")
    private String BidCount;

    @XmlAttribute(name = "Price")
    private String Price;

    @XmlAttribute(name = "ValuationPrice")
    private String ValuationPrice;

    @XmlAttribute(name = "TimeLeft")
    private String TimeLeft;

    @XmlAttribute(name = "RowID")
    private String RowID;

    String addTime;

    Boolean live;

    public Long getIdd() {
        return idd;
    }

    public void setIdd(Long idd) {
        this.idd = idd;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTraffic() {
        return Traffic;
    }

    public void setTraffic(String traffic) {
        Traffic = traffic;
    }

    public String getBidCount() {
        return BidCount;
    }

    public void setBidCount(String bidCount) {
        BidCount = bidCount;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getValuationPrice() {
        return ValuationPrice;
    }

    public void setValuationPrice(String valuationPrice) {
        ValuationPrice = valuationPrice;
    }

    public String getTimeLeft() {
        return TimeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        TimeLeft = timeLeft;
    }

    public String getRowID() {
        return RowID;
    }

    public void setRowID(String rowID) {
        RowID = rowID;
    }
}
