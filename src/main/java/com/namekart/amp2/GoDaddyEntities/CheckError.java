package com.namekart.amp2.GoDaddyEntities;

import java.util.List;

public class CheckError {
    String code,message;
    Integer retryAfterSec;

    public Integer getRetryAfterSec() {
        return retryAfterSec;
    }

    public void setRetryAfterSec(Integer retryAfterSec) {
        this.retryAfterSec = retryAfterSec;
    }

    List<CheckErrorField> fields;

    public String getCode() {
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

    public List<CheckErrorField> getFields() {
        return fields;
    }

    public void setFields(List<CheckErrorField> fields) {
        this.fields = fields;
    }
}
