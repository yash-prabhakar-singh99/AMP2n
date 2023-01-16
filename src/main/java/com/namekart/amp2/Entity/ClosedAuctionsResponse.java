package com.namekart.amp2.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class ClosedAuctionsResponse {
    Integer ResponseCode;
   // @JsonProperty("Status")
    String Status;
    List<ClosedAuctionsDets> Auctions;

    public ClosedAuctionsResponse() {
    }

    public Integer getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(Integer responseCode) {
        ResponseCode = responseCode;
    }

    //@JsonProperty("Status")
    public String getStatus() {
        return Status;
    }
    //@JsonProperty("Status")
    public void setStatus(String status) {
        Status = status;
    }

    public List<ClosedAuctionsDets> getAuctions() {
        return Auctions;
    }

    public void setAuctions(List<ClosedAuctionsDets> auctions) {
        Auctions = auctions;
    }
}
