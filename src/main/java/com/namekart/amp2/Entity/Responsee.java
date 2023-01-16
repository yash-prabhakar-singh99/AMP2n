package com.namekart.amp2.Entity;

public class Responsee {
    String ResponseCode;
    String Error;

    @Override
    public String toString() {
        return "Responsee{" +
                "ResponseCode='" + ResponseCode + '\'' +
                ", Error='" + Error + '\'' +
                '}';
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    public Responsee() {
    }

    public Responsee(String responseCode, String error) {
        ResponseCode = responseCode;
        Error = error;
    }
}
