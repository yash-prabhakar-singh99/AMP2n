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
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rowsPerPage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="beginsWithKeyword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "pageNumber",
    "rowsPerPage",
    "beginsWithKeyword"
})
@XmlRootElement(name = "GetAuctionList")
public class GetAuctionList {

    protected String pageNumber;
    protected String rowsPerPage;
    protected String beginsWithKeyword;

    /**
     * Gets the value of the pageNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPageNumber(String value) {
        this.pageNumber = value;
    }

    /**
     * Gets the value of the rowsPerPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowsPerPage() {
        return rowsPerPage;
    }

    /**
     * Sets the value of the rowsPerPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowsPerPage(String value) {
        this.rowsPerPage = value;
    }

    /**
     * Gets the value of the beginsWithKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeginsWithKeyword() {
        return beginsWithKeyword;
    }

    /**
     * Sets the value of the beginsWithKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeginsWithKeyword(String value) {
        this.beginsWithKeyword = value;
    }

}
