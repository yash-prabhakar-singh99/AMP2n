package com.namekart.amp2.GoDaddyEntities;

public class ErrorGDV429 {
    String code,message;
    int retryAfterSec;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRetryAfterSec() {
        return retryAfterSec;
    }

    public void setRetryAfterSec(int retryAfterSec) {
        this.retryAfterSec = retryAfterSec;
    }
}
