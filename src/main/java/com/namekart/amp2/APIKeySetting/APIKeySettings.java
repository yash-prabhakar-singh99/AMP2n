package com.namekart.amp2.APIKeySetting;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class APIKeySettings {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public APIKeySettings(int id, String nsKey, String dcId, String dcSecret, String ddKey, String ncKey, String gdKey, String gdSecret) {
        this.id = id;
        this.nsKey = nsKey;
        this.dcId = dcId;
        this.dcSecret = dcSecret;
        this.ddKey = ddKey;
        this.ncKey = ncKey;
        this.gdKey = gdKey;
        this.gdSecret = gdSecret;
    }

    public APIKeySettings() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int id;
    String nsKey,dcId,dcSecret,ddKey,ncKey,gdKey,gdSecret;



    public String getNsKey() {
        return nsKey;
    }

    public void setNsKey(String nsKey) {
        this.nsKey = nsKey;
    }

    public String getDcId() {
        return dcId;
    }

    public void setDcId(String dcId) {
        this.dcId = dcId;
    }

    public String getDcSecret() {
        return dcSecret;
    }

    public void setDcSecret(String dcSecret) {
        this.dcSecret = dcSecret;
    }

    public String getDdKey() {
        return ddKey;
    }

    public void setDdKey(String ddKey) {
        this.ddKey = ddKey;
    }

    public String getNcKey() {
        return ncKey;
    }

    public void setNcKey(String ncKey) {
        this.ncKey = ncKey;
    }

    public String getGdKey() {
        return gdKey;
    }

    public void setGdKey(String gdKey) {
        this.gdKey = gdKey;
    }

    public String getGdSecret() {
        return gdSecret;
    }

    public void setGdSecret(String gdSecret) {
        this.gdSecret = gdSecret;
    }
}
