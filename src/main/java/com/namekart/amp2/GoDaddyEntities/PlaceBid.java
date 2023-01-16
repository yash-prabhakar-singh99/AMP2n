package com.namekart.amp2.GoDaddyEntities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="PlaceBid")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaceBid implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name="IsValid")
    private String IsValid;

    @XmlAttribute(name="Message")
    private String Message;

    @XmlAttribute(name="AuditDateTime")
    private String AuditDateTime;

    @XmlAttribute(name="IsHighBid")
    private String IsHighBid;

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getAuditDateTime() {
        return AuditDateTime;
    }

    public void setAuditDateTime(String auditDateTime) {
        AuditDateTime = auditDateTime;
    }

    public String getIsHighBid() {
        return IsHighBid;
    }

    public void setIsHighBid(String isHighBid) {
        IsHighBid = isHighBid;
    }
}
