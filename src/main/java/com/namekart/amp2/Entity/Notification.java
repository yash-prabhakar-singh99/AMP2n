package com.namekart.amp2.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long ID;

    String message;

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
