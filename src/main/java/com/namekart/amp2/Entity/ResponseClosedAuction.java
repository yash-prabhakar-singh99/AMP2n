package com.namekart.amp2.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class ResponseClosedAuction {

    ClosedAuctionsResponse GetClosedAuctionsResponse;

    public ResponseClosedAuction() {
    }

   //@JsonProperty("GetClosedAuctionsResponse")
    public ClosedAuctionsResponse getGetClosedAuctionsResponse() {
        return GetClosedAuctionsResponse;
    }
  // @JsonProperty("GetClosedAuctionsResponse")
    public void setGetClosedAuctionsResponse(ClosedAuctionsResponse getClosedAuctionsResponse) {
        GetClosedAuctionsResponse = getClosedAuctionsResponse;
    }
}
