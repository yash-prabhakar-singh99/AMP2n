package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlElement;

public class SiloAucReply {
    Integer code;
    String detail;
    @XmlElement(name = "body")
    SiloAuctionDetails body;

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

    public SiloAuctionDetails getBody() {
        return body;
    }

    public void setBody(SiloAuctionDetails body) {
        this.body = body;
    }
}
