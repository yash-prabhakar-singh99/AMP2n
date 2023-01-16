package com.namekart.amp2.DCEntity;

import java.util.List;

public class ResponseAuctionresult {
    List<AuctionResultdc> items;
    int totalRecords;
    Cursor cursor;

    public ResponseAuctionresult() {
    }

    public List<AuctionResultdc> getItems() {
        return items;
    }

    public void setItems(List<AuctionResultdc> items) {
        this.items = items;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
