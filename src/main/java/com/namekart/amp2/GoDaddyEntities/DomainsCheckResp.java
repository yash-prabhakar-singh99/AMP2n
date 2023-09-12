package com.namekart.amp2.GoDaddyEntities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DomainsCheckResp {

    List<DomainCheck> domains;

   // DomainCheck[] domains;



    public DomainsCheckResp() {
    }

   public DomainsCheckResp(List<DomainCheck> domains) {
        this.domains = domains;
    }

    @JsonProperty(value = "domains")
    public List<DomainCheck> getDomains() {
        return domains;
    }
    @JsonProperty(value = "domains")
    public void setDomains(List<DomainCheck> domains) {
        this.domains = domains;
    }
}
