package com.namekart.amp2.GoDaddyEntities;

public class DomainCheck {
    Boolean available,definitive;
    String domain;

    public Boolean getAvailable() {
        return available;
    }

    public DomainCheck(Boolean available, Boolean definitive, String domain) {
        this.available = available;
        this.definitive = definitive;
        this.domain = domain;
    }

    public DomainCheck() {
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getDefinitive() {
        return definitive;
    }

    public void setDefinitive(Boolean definitive) {
        this.definitive = definitive;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
