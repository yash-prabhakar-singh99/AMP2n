package com.namekart.amp2.NamecheapEntity;

import java.util.List;

public class ResponseLivedb {
    Pages pages;
    List<Livencdb> items;

    public ResponseLivedb() {
    }

    public Pages getPages() {
        return pages;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    public List<Livencdb> getItems() {
        return items;
    }

    public void setItems(List<Livencdb> items) {
        this.items = items;
    }
}
