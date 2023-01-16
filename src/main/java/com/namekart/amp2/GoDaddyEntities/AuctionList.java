package com.namekart.amp2.GoDaddyEntities;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "AuctionList")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuctionList {
    @XmlAttribute(name = "IsValid")
    private String IsValid;

    @XmlAttribute(name = "TotalRecords")
    private String TotalRecords;

@XmlElement(name = "Auction")
List<Lauction> lauctionList;

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getTotalRecords() {
        return TotalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        TotalRecords = totalRecords;
    }

    public List<Lauction> getLauctionList() {
        return lauctionList;
    }

    public void setLauctionList(List<Lauction> lauctionList) {
        this.lauctionList = lauctionList;
    }
}
