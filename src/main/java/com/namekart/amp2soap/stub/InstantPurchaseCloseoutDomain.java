//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.12 at 06:40:39 PM IST 
//


package com.namekart.amp2soap.stub;

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
 *         &lt;element name="closeoutDomainPriceKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "closeoutDomainPriceKey",
    "acceptUTOS",
    "acceptAMA",
    "acceptDNRA"
})
@XmlRootElement(name = "InstantPurchaseCloseoutDomain")
public class InstantPurchaseCloseoutDomain {

    protected String domainName;
    protected String closeoutDomainPriceKey;
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
     * Gets the value of the closeoutDomainPriceKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloseoutDomainPriceKey() {
        return closeoutDomainPriceKey;
    }

    /**
     * Sets the value of the closeoutDomainPriceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloseoutDomainPriceKey(String value) {
        this.closeoutDomainPriceKey = value;
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
