//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.07 at 01:17:40 AM IST 
//


package com.namekart.amp2.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="domainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bidAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bidComments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="useMyPurchaseProfile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="acceptUTOS" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="acceptAMA" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="acceptDNRA" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "domainName",
    "bidAmount",
    "bidComments",
    "useMyPurchaseProfile",
    "acceptUTOS",
    "acceptAMA",
    "acceptDNRA"
})
@XmlRootElement(name = "PlaceBidWithPurchaseProfile")
public class PlaceBidWithPurchaseProfile {

    protected String domainName;
    protected String bidAmount;
    protected String bidComments;
    protected boolean useMyPurchaseProfile;
    protected boolean acceptUTOS;
    protected boolean acceptAMA;
    protected boolean acceptDNRA;

    /**
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the bidAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBidAmount() {
        return bidAmount;
    }

    /**
     * Sets the value of the bidAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBidAmount(String value) {
        this.bidAmount = value;
    }

    /**
     * Gets the value of the bidComments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBidComments() {
        return bidComments;
    }

    /**
     * Sets the value of the bidComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBidComments(String value) {
        this.bidComments = value;
    }

    /**
     * Gets the value of the useMyPurchaseProfile property.
     * 
     */
    public boolean isUseMyPurchaseProfile() {
        return useMyPurchaseProfile;
    }

    /**
     * Sets the value of the useMyPurchaseProfile property.
     * 
     */
    public void setUseMyPurchaseProfile(boolean value) {
        this.useMyPurchaseProfile = value;
    }

    /**
     * Gets the value of the acceptUTOS property.
     * 
     */
    public boolean isAcceptUTOS() {
        return acceptUTOS;
    }

    /**
     * Sets the value of the acceptUTOS property.
     * 
     */
    public void setAcceptUTOS(boolean value) {
        this.acceptUTOS = value;
    }

    /**
     * Gets the value of the acceptAMA property.
     * 
     */
    public boolean isAcceptAMA() {
        return acceptAMA;
    }

    /**
     * Sets the value of the acceptAMA property.
     * 
     */
    public void setAcceptAMA(boolean value) {
        this.acceptAMA = value;
    }

    /**
     * Gets the value of the acceptDNRA property.
     * 
     */
    public boolean isAcceptDNRA() {
        return acceptDNRA;
    }

    /**
     * Sets the value of the acceptDNRA property.
     * 
     */
    public void setAcceptDNRA(boolean value) {
        this.acceptDNRA = value;
    }

}
