package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="namesilo")
public class SiloRespAucList {

    @XmlElement(name = "reply")
    SiloAucListReply reply;

    public SiloAucListReply getReply() {
        return reply;
    }

    public void setReply(SiloAucListReply reply) {
        this.reply = reply;
    }
}
