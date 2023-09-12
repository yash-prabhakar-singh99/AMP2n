package com.namekart.amp2.DotDBEntity;

import java.util.ArrayList;

public class DdMatch {
    public String name;
    public int count;
    public ArrayList<String> suffixes;
    public DdSiteStatus site_status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<String> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(ArrayList<String> suffixes) {
        this.suffixes = suffixes;
    }

    public DdSiteStatus getSite_status() {
        return site_status;
    }

    public void setSite_status(DdSiteStatus site_status) {
        this.site_status = site_status;
    }
}
