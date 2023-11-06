package com.namekart.amp2.GoDaddyEntities;

import java.util.ArrayList;
import java.util.List;

public class PurchaseInfo {
    private Consent consent;
    private Contact contactAdmin;
    private Contact contactBilling;
    private Contact contactRegistrant;
    private Contact contactTech;
    private String domain;
    private List<String> nameServers = new ArrayList<String>();
    private Integer period;
    private Boolean privacy;
    private Boolean renewAuto;
    public Consent getConsent() {
        return consent;
    }
    public void setConsent(Consent consent) {
        this.consent = consent;
    }
    public Contact getContactAdmin() {
        return contactAdmin;
    }
    public void setContactAdmin(Contact contactAdmin) {
        this.contactAdmin = contactAdmin;
    }
    public Contact getContactBilling() {
        return contactBilling;
    }
    public void setContactBilling(Contact contactBilling) {
        this.contactBilling = contactBilling;
    }
    public Contact getContactRegistrant() {
        return contactRegistrant;
    }
    public void setContactRegistrant(Contact contactRegistrant) {
        this.contactRegistrant = contactRegistrant;
    }
    public Contact getContactTech() {
        return contactTech;
    }

    public PurchaseInfo(Consent consent, Contact contactAdmin, Contact contactBilling, Contact contactRegistrant, Contact contactTech, String domain, List<String> nameServers, Integer period, Boolean privacy, Boolean renewAuto) {
        this.consent = consent;
        this.contactAdmin = contactAdmin;
        this.contactBilling = contactBilling;
        this.contactRegistrant = contactRegistrant;
        this.contactTech = contactTech;
        this.domain = domain;
        this.nameServers = nameServers;
        this.period = period;
        this.privacy = privacy;
        this.renewAuto = renewAuto;
    }

    public PurchaseInfo() {
    }

    public void setContactTech(Contact contactTech) {
        this.contactTech = contactTech;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public List<String> getNameServers() {
        return nameServers;
    }
    public void setNameServers(List<String> nameServers) {
        this.nameServers = nameServers;
    }
    public Integer getPeriod() {
        return period;
    }
    public void setPeriod(Integer period) {
        this.period = period;
    }
    public Boolean getPrivacy() {
        return privacy;
    }
    public void setPrivacy(Boolean privacy) {
        this.privacy = privacy;
    }
    public Boolean getRenewAuto() {
        return renewAuto;
    }
    public void setRenewAuto(Boolean renewAuto) {
        this.renewAuto = renewAuto;
    }
}
