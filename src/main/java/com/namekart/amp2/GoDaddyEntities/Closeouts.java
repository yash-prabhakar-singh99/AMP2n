package com.namekart.amp2.GoDaddyEntities;

import java.util.List;

public class Closeouts {
    List<String> closeout;
    String price;

    public Closeouts() {
    }

    public Closeouts(List<String> closeout, String price) {
        this.closeout = closeout;
        this.price = price;
    }

    public List<String> getCloseout() {
        return closeout;
    }

    public void setCloseout(List<String> closeout) {
        this.closeout = closeout;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
