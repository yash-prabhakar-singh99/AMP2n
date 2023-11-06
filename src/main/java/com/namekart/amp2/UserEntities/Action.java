package com.namekart.amp2.UserEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.Notification;

import javax.persistence.*;

@Entity
public class Action
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String action;

    String medium;

    String telegramGroup;

    public Action(String action, String medium, String telegramGroup, User user, DBdetails dbdetails, Notification notification, boolean success, String domain, String userName) {
        this.action = action;
        this.medium = medium;
        this.telegramGroup = telegramGroup;
        this.user = user;
        this.dbdetails = dbdetails;
        this.notification = notification;
        this.success = success;
        this.domain = domain;
        this.userName = userName;
    }

    public Action() {
    }

    public Action(String action, String medium, User user, DBdetails dbdetails, Notification notification, boolean success, String domain, String userName) {
        this.action = action;
        this.medium = medium;
        this.user = user;
        this.dbdetails = dbdetails;
        this.notification = notification;
        this.success = success;
        this.domain = domain;
        this.userName = userName;
    }

    public String getTelegramGroup() {
        return telegramGroup;
    }

    public void setTelegramGroup(String telegramGroup) {
        this.telegramGroup = telegramGroup;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DBdetails getDbdetails() {
        return dbdetails;
    }

    public void setDbdetails(DBdetails dbdetails) {
        this.dbdetails = dbdetails;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="dbdetails_id", nullable=false)
    DBdetails dbdetails;

    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "notif_id", referencedColumnName = "id")
    Notification notification;

    boolean success;

    String domain;

    String userName;

}
