package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class SiloAucListReply {
    @XmlElement(name="detail")
    String detail;
    @XmlElement(name="code")
    Integer code;

    @XmlElementWrapper(name = "body")
    @XmlElement(name = "entry")
    List<SiloAuctionDetails> body;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<SiloAuctionDetails> getBody() {
        return body;
    }

    public void setBody(List<SiloAuctionDetails> body) {
        this.body = body;
    }
}
