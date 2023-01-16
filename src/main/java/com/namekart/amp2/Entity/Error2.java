package com.namekart.amp2.Entity;

public class Error2 {
    String status;
    String[] content;
    public Error2() {
    }

    public Error2(String status, String[] content) {
        this.status = status;
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }
}
