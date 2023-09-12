package com.namekart.amp2.DCEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackOrderform {
    String Domain,Type;
    int Amount;

    public BackOrderform(String domain, String type, int amount) {
        Domain = domain;
        Type = type;
        Amount = amount;
    }

    public BackOrderform() {
    }

    @JsonProperty("Domain")
    public String getDomain() {
        return Domain;
    }

    @JsonProperty("Domain")
    public void setDomain(String domain) {
        Domain = domain;
    }
    @JsonProperty("Type")
    public String getType() {
        return Type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        Type = type;
    }

    @JsonProperty("Amount")
    public int getAmount() {
        return Amount;
    }

    @JsonProperty("Amount")
    public void setAmount(int amount) {
        Amount = amount;
    }
}
