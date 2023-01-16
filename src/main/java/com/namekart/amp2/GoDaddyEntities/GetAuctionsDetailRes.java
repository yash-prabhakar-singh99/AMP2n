package com.namekart.amp2.GoDaddyEntities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="AuctionDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAuctionsDetailRes implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @XmlAttribute(name="IsValid")
    private String IsValid;

    @XmlAttribute(name="DomainName")
    private String DomainName;

    @XmlAttribute(name="Message")
    private String Message;


    @XmlAttribute(name="AuctionEndTime")
    private String AuctionEndTime;

    @XmlAttribute(name="BidCount")
    private String BidCount;

    @XmlAttribute(name="Price")
    private String Price;

    @XmlAttribute(name="ValuationPrice")
    private String ValuationPrice;

    @XmlAttribute(name="Traffic")
    private String Traffic;

    @XmlAttribute(name="CreateDate")
    private String CreateDate;

    @XmlAttribute(name="BidIncrementAmount")
    private String BidIncrementAmount;

    @XmlAttribute(name="AuctionModel")
    private String AuctionModel;

    @XmlAttribute(name="AuditDateTime")
    private String AuditDateTime;

    @XmlAttribute(name="IsHighestBidder")
    private String IsHighestBidder;

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getDomainName() {
        return DomainName;
    }

    public void setDomainName(String domainName) {
        DomainName = domainName;
    }

    public String getAuctionEndTime() {
        return AuctionEndTime;
    }

    public void setAuctionEndTime(String auctionEndTime) {
        AuctionEndTime = auctionEndTime;
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

    public String getTraffic() {
        return Traffic;
    }

    public void setTraffic(String traffic) {
        Traffic = traffic;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getBidIncrementAmount() {
        return BidIncrementAmount;
    }

    public void setBidIncrementAmount(String bidIncrementAmount) {
        BidIncrementAmount = bidIncrementAmount;
    }

    public String getAuctionModel() {
        return AuctionModel;
    }

    public void setAuctionModel(String auctionModel) {
        AuctionModel = auctionModel;
    }

    public String getAuditDateTime() {
        return AuditDateTime;
    }

    public void setAuditDateTime(String auditDateTime) {
        AuditDateTime = auditDateTime;
    }

    public String getIsHighestBidder() {
        return IsHighestBidder;
    }

    public void setIsHighestBidder(String isHighestBidder) {
        IsHighestBidder = isHighestBidder;
    }

    public GetAuctionsDetailRes() {
        super();
    }
}
