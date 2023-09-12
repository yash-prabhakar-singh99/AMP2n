package com.namekart.amp2.UserEntities;

import java.util.ArrayList;

public class JwtClaimsSet {
    public Claims claims;
    public Object notBeforeTime;
    public Object issueTime;
    public Object jwtid;
    public ArrayList<String> audience;
    public Object expirationTime;
    public String issuer;
    public String subject;

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public Object getNotBeforeTime() {
        return notBeforeTime;
    }

    public void setNotBeforeTime(Object notBeforeTime) {
        this.notBeforeTime = notBeforeTime;
    }

    public Object getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Object issueTime) {
        this.issueTime = issueTime;
    }

    public Object getJwtid() {
        return jwtid;
    }

    public void setJwtid(Object jwtid) {
        this.jwtid = jwtid;
    }

    public ArrayList<String> getAudience() {
        return audience;
    }

    public void setAudience(ArrayList<String> audience) {
        this.audience = audience;
    }

    public Object getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Object expirationTime) {
        this.expirationTime = expirationTime;
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
}
