package com.namekart.amp2.Entity;

import java.util.List;

public class Response_AuctionDetails {

    String status,size;

List<Auction_details> auction_details;

    public List<String> getContent() {
        return content;
    }
    public String getCont() {
        return content.get(0);
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    List<String> content;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<Auction_details> getAuction_details() {
        return auction_details;
    }

    @Override
    public String toString() {
        return "Response_AuctionDetails{" +
                "status='" + status + '\'' +
                ", size='" + size + '\'' +
                ", auction_details=" + auction_details +
                ", content=" + content +
                '}';
    }

    public Auction_details getAuction_det() {
        return auction_details.get(0);
    }

    public void setAuction_details(List<Auction_details> auction_details) {
        this.auction_details = auction_details;
    }
}
