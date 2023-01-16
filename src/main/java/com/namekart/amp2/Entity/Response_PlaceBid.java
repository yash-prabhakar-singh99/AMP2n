package com.namekart.amp2.Entity;

public class Response_PlaceBid
{
    String status;

    Auction_details auction_details;

    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Response_PlaceBid(String status, Auction_details auction_details) {
        this.status = status;
        this.auction_details = auction_details;
    }

    public Response_PlaceBid() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Auction_details getAuction_details() {
        return auction_details;
    }

    public void setAuction_details(Auction_details auction_details) {
        this.auction_details = auction_details;
    }

}
