package com.namekart.amp2.NamecheapEntity;

import java.util.List;

public class ResponseAuctionDetailsNC {
    Pages pages;
    List<AuctionDetailNC> items;

    public ResponseAuctionDetailsNC() {
    }

    public Pages getPages() {
        return pages;
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    public List<AuctionDetailNC> getItems() {
        return items;
    }

    public void setItems(List<AuctionDetailNC> items) {
        this.items = items;
    }
}
