package com.namekart.amp2.DCEntity;

import java.util.List;

public class ResponseAuctionList {
    List<AuctionDetailDC> items;
    Long totalRecords;
Cursor cursor;

    public ResponseAuctionList() {
    }

    public List<AuctionDetailDC> getItems() {
        return items;
    }

    public void setItems(List<AuctionDetailDC> items) {
        this.items = items;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
