package com.namekart.amp2.GoDaddyEntities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="InstantPurchaseCloseoutDomain")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstantPurchaseCloseout {
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name="Result")
    private String Result;

    @XmlAttribute(name="Message")
    private String Message;

    @XmlAttribute(name="Domain")
    private String Domain;

    @XmlAttribute(name="Price")
    private String Price;

    @XmlAttribute(name="PrivateRegistration")
    private String PrivateRegistration;

    @XmlAttribute(name="ICANNFee")
    private String ICANNFee;

    @XmlAttribute(name="Taxes")
    private String Taxes;

    @XmlAttribute(name="Total")
    private String Total;

    @XmlAttribute(name="OrderID")
    private String  OrderID;

    @XmlAttribute(name="RenewalPrice")
    private String RenewalPrice;

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPrivateRegistration() {
        return PrivateRegistration;
    }

    public void setPrivateRegistration(String privateRegistration) {
        PrivateRegistration = privateRegistration;
    }

    public String getICANNFee() {
        return ICANNFee;
    }

    public void setICANNFee(String ICANNFee) {
        this.ICANNFee = ICANNFee;
    }

    public String getTaxes() {
        return Taxes;
    }

    public void setTaxes(String taxes) {
        Taxes = taxes;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getRenewalPrice() {
        return RenewalPrice;
    }

    public void setRenewalPrice(String renewalPrice) {
        RenewalPrice = renewalPrice;
    }
}
