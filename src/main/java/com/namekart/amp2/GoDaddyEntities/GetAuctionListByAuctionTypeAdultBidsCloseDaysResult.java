package com.namekart.amp2.GoDaddyEntities;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class GetAuctionListByAuctionTypeAdultBidsCloseDaysResult {

@XmlElement(name = "AuctionList")
AuctionList AuctionList;

    public AuctionList getAuctionList() {
        return AuctionList;
    }

    public void setAuctionList(AuctionList auctionList) {
        AuctionList = auctionList;
    }
}
