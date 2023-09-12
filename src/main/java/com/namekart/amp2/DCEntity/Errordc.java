package com.namekart.amp2.DCEntity;

public class Errordc {
    String errorCode;
    String description;

    public String getInnerError() {
        return innerError;
    }

    public void setInnerError(String innerError) {
        this.innerError = innerError;
    }

    String innerError;

    public Errordc() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
