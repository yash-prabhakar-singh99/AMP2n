package com.namekart.amp2.NamesiloEntities;

import java.util.List;

public class SiloRenewReply {
    Integer code;
    String detail;
    String domain;
    String message;
    Float order_amount;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDomains() {
        return domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Float getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(Float order_amount) {
        this.order_amount = order_amount;
    }

    public void setDomains(String domain) {
        this.domain = domain;
    }
}
