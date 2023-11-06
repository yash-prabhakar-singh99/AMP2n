package com.namekart.amp2.NamesiloEntities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "namesilo")
public class SiloRespPlaceBid {

    SiloPlaceBidReply reply;

    public SiloPlaceBidReply getReply() {
        return reply;
    }

    public void setReply(SiloPlaceBidReply reply) {
        this.reply = reply;
    }
}
