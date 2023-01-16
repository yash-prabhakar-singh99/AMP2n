package com.namekart.amp2.NamesiloEntities;

import java.util.List;

public class SiloDomainListReply {
    Integer code;
    String detail;
    List<String> domains;

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

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }
}
