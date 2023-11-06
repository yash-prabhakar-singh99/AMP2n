package com.namekart.amp2.GoDaddyEntities;

import java.util.ArrayList;
import java.util.List;

public class Consent {
    private String agreedAt;
    private String agreedBy;

    public Consent(String agreedAt, String agreedBy, List<String> agreementKeys) {
        this.agreedAt = agreedAt;
        this.agreedBy = agreedBy;
        this.agreementKeys = agreementKeys;
    }

    public Consent() {
    }

    private List<String> agreementKeys = new ArrayList<String>();
    public String getAgreedAt() {
        return agreedAt;
    }
    public void setAgreedAt(String agreedAt) {
        this.agreedAt = agreedAt;
    }
    public String getAgreedBy() {
        return agreedBy;
    }
    public void setAgreedBy(String agreedBy) {
        this.agreedBy = agreedBy;
    }
    public List<String> getAgreementKeys() {
        return agreementKeys;
    }
    public void setAgreementKeys(List<String> agreementKeys) {
        this.agreementKeys = agreementKeys;
    }

}
