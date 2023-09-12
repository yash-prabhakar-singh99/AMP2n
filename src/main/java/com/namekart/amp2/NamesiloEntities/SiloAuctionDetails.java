package com.namekart.amp2.NamesiloEntities;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@Entity
public class SiloAuctionDetails {


    //@XmlElement(name="id")
    @Column(unique = true)
    @JacksonXmlProperty(localName = "id")
    Long nsid;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JacksonXmlProperty(localName = "idd")
    //@XmlElement(name="idd")
    Long id;
    Boolean initialList;
    Boolean endList;

    public Boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    Boolean highlight;

    Integer EST;

    public Integer getEST() {
        return EST;
    }

    public void setEST(Integer EST) {
        this.EST = EST;
    }

    public Boolean getInitialList() {
        return initialList;
    }

    public void setInitialList(Boolean initialList) {
        this.initialList = initialList;
    }

    public Boolean getEndList() {
        return endList;
    }

    public void setEndList(Boolean endList) {
        this.endList = endList;
    }

    @Transient
    List<SiloError> errors;

    public List<SiloError> getErrors() {
        return errors;
    }

    public void setErrors(List<SiloError> errors) {
        this.errors = errors;
    }

    Boolean live;

    String addTime, timeLeft;

    public Long getNsid() {
        return nsid;
    }

    public void setNsid(Long nsid) {
        this.nsid = nsid;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    @XmlElement(name="leaderUserId")
    Long leaderUserId;
    @XmlElement(name="ownerUserId")
    Long ownerUserId;
    @XmlElement(name="domainId")
    Long domainId;
    @XmlElement(name="statusId")
    Integer statusId;
    @XmlElement(name="typeId")
    Integer typeId;
    @XmlElement(name="openingBid")
    Float openingBid;
    @XmlElement(name="currentBid")
    Float currentBid;
    @XmlElement(name="maxBid")
    Float maxBid;
    @XmlElement(name="domain")
    String domain;
    @XmlElement(name="domainCreatedOn")
    String domainCreatedOn;
    @XmlElement(name="auctionEndsOn")
    String auctionEndsOn;

    Integer gdv;

    public Integer getGdv() {
        return gdv;
    }

    public void setGdv(Integer gdv) {
        this.gdv = gdv;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name="url")
    String url;

    Integer bidsQuantity;

    public Integer getBidsQuantity() {
        return bidsQuantity;
    }

    public void setBidsQuantity(Integer bidsQuantity) {
        this.bidsQuantity = bidsQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Long leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Float getOpeningBid() {
        return openingBid;
    }

    public void setOpeningBid(Float openingBid) {
        this.openingBid = openingBid;
    }

    public Float getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(Float currentBid) {
        this.currentBid = currentBid;
    }

    public Float getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(Float maxBid) {
        this.maxBid = maxBid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomainCreatedOn() {
        return domainCreatedOn;
    }

    public void setDomainCreatedOn(String domainCreatedOn) {
        this.domainCreatedOn = domainCreatedOn;
    }

    public String getAuctionEndsOn() {
        return auctionEndsOn;
    }

    public void setAuctionEndsOn(String auctionEndsOn) {
        this.auctionEndsOn = auctionEndsOn;
    }

    public SiloAuctionDetails() {
    }
}
