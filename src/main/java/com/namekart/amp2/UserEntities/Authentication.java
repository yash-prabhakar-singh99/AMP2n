package com.namekart.amp2.UserEntities;

import java.util.ArrayList;

public class Authentication {
    public ArrayList<Authority> authorities;
    public Headers headers;
    public Attributes attributes;
    public String tokenValue;
    public JwtClaimsSet jwtClaimsSet;
    public String name;
    public Claims claims;
    public boolean personalAccount;
    public String issuer;
    public String subject;
    public String tenantId;

    public ArrayList<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(ArrayList<Authority> authorities) {
        this.authorities = authorities;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public JwtClaimsSet getJwtClaimsSet() {
        return jwtClaimsSet;
    }

    public void setJwtClaimsSet(JwtClaimsSet jwtClaimsSet) {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public boolean isPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(boolean personalAccount) {
        this.personalAccount = personalAccount;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
