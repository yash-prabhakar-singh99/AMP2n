package com.namekart.amp2.DCEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackOrderFailure {
    String domain;
    Errordc error;


    public String getDomain() {
        return domain;
    }


    public void setDomain(String domain) {
        domain = domain;
    }


    public Errordc getError() {
        return error;
    }

    public void setError(Errordc error) {
        error = error;
    }
}
