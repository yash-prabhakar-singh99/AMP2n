package com.namekart.amp2.DotDBEntity;

import java.util.ArrayList;

public class DdSiteStatus {
    public ArrayList<String> active_suffixes;
    public int active_count;
    public ArrayList<String> parked_suffixes;
    public int parked_count;
    public ArrayList<String> inactive_suffixes;
    public int inactive_count;

    public ArrayList<String> getActive_suffixes() {
        return active_suffixes;
    }

    public void setActive_suffixes(ArrayList<String> active_suffixes) {
        this.active_suffixes = active_suffixes;
    }

    public int getActive_count() {
        return active_count;
    }

    public void setActive_count(int active_count) {
        this.active_count = active_count;
    }

    public ArrayList<String> getParked_suffixes() {
        return parked_suffixes;
    }

    public void setParked_suffixes(ArrayList<String> parked_suffixes) {
        this.parked_suffixes = parked_suffixes;
    }

    public int getParked_count() {
        return parked_count;
    }

    public void setParked_count(int parked_count) {
        this.parked_count = parked_count;
    }

    public ArrayList<String> getInactive_suffixes() {
        return inactive_suffixes;
    }

    public void setInactive_suffixes(ArrayList<String> inactive_suffixes) {
        this.inactive_suffixes = inactive_suffixes;
    }

    public int getInactive_count() {
        return inactive_count;
    }

    public void setInactive_count(int inactive_count) {
        this.inactive_count = inactive_count;
    }
}
