package com.namekart.amp2.Entity;

import java.util.List;

public class ResponseLive {
    String status;
    List<LiveDetails> auction_list;

    public ResponseLive() {
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAuction_list(List<LiveDetails> auction_list) {
        this.auction_list = auction_list;
    }

    public String getStatus() {
        return status;
    }

    public List<LiveDetails> getAuction_list() {
        return auction_list;
    }
}
