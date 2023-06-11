package com.namekart.amp2.Entity;

import java.util.List;

public class FetchReq {
    List<String> domains;
    Boolean watch;

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public Boolean getWatch() {
        return watch;
    }

    public void setWatch(Boolean watch) {
        this.watch = watch;
    }
}
