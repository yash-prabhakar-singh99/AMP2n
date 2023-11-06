package com.namekart.amp2.GoDaddyEntities;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "MyWonSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class MyWonSummary {
    @XmlAttribute(name = "IsValid")
    private String IsValid;

    @XmlAttribute(name = "TotalRecords")
    private Integer TotalRecords;

    @XmlElement(name = "Auction")
    List<Lauction> lauctionList;

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public Integer getTotalRecords() {
        return TotalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        TotalRecords = totalRecords;
    }

    public List<Lauction> getLauctionList() {
        return lauctionList;
    }

    public void setLauctionList(List<Lauction> lauctionList) {
        this.lauctionList = lauctionList;
    }
}