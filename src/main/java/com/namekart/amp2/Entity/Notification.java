package com.namekart.amp2.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.namekart.amp2.UserEntities.Action;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long ID;

    String message;

    String platform;

    @JsonIgnore
    @OneToOne(mappedBy = "notification")
    Action action;

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @Temporal(TemporalType.DATE)
    Date date;

    @Temporal(TemporalType.TIME)
    Date time;

    String times;
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Notification(String platform,String times ,String message)
    {
        //SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");
        //TimeZone ist = TimeZone.getTimeZone("IST");
        //timeft.setTimeZone(ist);
        this.date= new Date();
        this.time=new Date();
        //this.times = timeft.format(date);
        this.times= times;
        this.platform= platform;
        this.message = times+" | "+platform+": "+message;
    }

    public Notification(String message) {
        this.message = message;
    }

    public Notification() {
    }

    public Long getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
