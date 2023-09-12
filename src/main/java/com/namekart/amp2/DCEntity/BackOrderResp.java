package com.namekart.amp2.DCEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BackOrderResp {
    List<BackOrderSuccess> successes;
    List<BackOrderFailure> failures;

   // @JsonProperty("Successes")
    public List<BackOrderSuccess> getSuccesses() {
        return successes;
    }
    //@JsonProperty("Successes")
    public void setSuccesses(List<BackOrderSuccess> successes) {
        successes = successes;
    }

    public List<BackOrderFailure> getFailures() {
        return failures;
    }

    public BackOrderResp(List<BackOrderSuccess> successes, List<BackOrderFailure> failures) {
        this.successes = successes;
        this.failures = failures;
    }

    public BackOrderResp() {
    }

    public void setFailures(List<BackOrderFailure> failures) {
        failures = failures;
    }
}
