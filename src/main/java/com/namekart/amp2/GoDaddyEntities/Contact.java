package com.namekart.amp2.GoDaddyEntities;

public class Contact {
    private AddressMailing addressMailing;
    private String email;
    private String fax;
    private String jobTitle;
    private String nameFirst;
    private String nameLast;
    private String nameMiddle;
    private String organization;
    private String phone;
    public AddressMailing getAddressMailing() {
        return addressMailing;
    }
    public void setAddressMailing(AddressMailing addressMailing) {
        this.addressMailing = addressMailing;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getNameFirst() {
        return nameFirst;
    }
    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }
    public String getNameLast() {
        return nameLast;
    }
    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }
    public String getNameMiddle() {
        return nameMiddle;
    }
    public void setNameMiddle(String nameMiddle) {
        this.nameMiddle = nameMiddle;
    }
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Contact(AddressMailing addressMailing, String email, String fax, String jobTitle, String nameFirst, String nameLast, String nameMiddle, String organization, String phone) {
        this.addressMailing = addressMailing;
        this.email = email;
        this.fax = fax;
        this.jobTitle = jobTitle;
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.nameMiddle = nameMiddle;
        this.organization = organization;
        this.phone = phone;
    }

    public Contact() {
    }
}
