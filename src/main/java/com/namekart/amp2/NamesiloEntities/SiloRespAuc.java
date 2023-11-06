package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="namesilo")
public class SiloRespAuc {
SiloAucReply reply;

    public SiloAucReply getReply() {
        return reply;
    }

    public void setReply(SiloAucReply reply) {
        this.reply = reply;
    }
}
