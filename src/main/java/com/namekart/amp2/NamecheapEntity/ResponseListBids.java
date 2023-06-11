package com.namekart.amp2.NamecheapEntity;

import java.util.List;

public class ResponseListBids {
    Pages pages;
    List<SaleBid> items;

    public ResponseListBids() {
    }

    public Pages getPages() {
        return pages;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    public List<SaleBid> getItems() {
        return items;
    }

    public void setItems(List<SaleBid> items) {
        this.items = items;
    }
}
