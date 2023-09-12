package com.namekart.amp2.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LiveDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    Integer EST;



    public Integer getEST() {
        return EST;
    }

    public void setEST(Integer EST) {
        this.EST = EST;
    }
    Boolean initialList;
    Boolean endList;

    public Boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    Boolean highlight;

    public Boolean getInitialList() {
        return initialList;
    }

    public void setInitialList(Boolean initialList) {
        this.initialList = initialList;
    }

    public Boolean getEndList() {
        return endList;
    }

    public void setEndList(Boolean endList) {
        this.endList = endList;
    }

    Integer gdv;

    public Integer getGdv() {
        return gdv;
    }

    public void setGdv(Integer gdv) {
        this.gdv = gdv;
    }

    String time_left;

    String addtime;

    Boolean live;

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    String platform;
    Long auction_id;

    String domain,current_bid_price, end_time,estibot_appraisal,utf_name;
    int bids,bidders,age;

    Long end_time_stamp;

    public LiveDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuction_id() {
        return auction_id;
    }

    public void setAuction_id(Long auction_id) {
        this.auction_id = auction_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCurrent_bid_price() {
        return current_bid_price;
    }

    public void setCurrent_bid_price(String current_bid_price) {
        this.current_bid_price = current_bid_price;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEstibot_appraisal() {
        return estibot_appraisal;
    }

    public void setEstibot_appraisal(String estibot_appraisal) {
        this.estibot_appraisal = estibot_appraisal;
    }

    public String getUtf_name() {
        return utf_name;
    }

    public void setUtf_name(String utf_name) {
        this.utf_name = utf_name;
    }

    public int getBids() {
        return bids;
    }

    public void setBids(int bids) {
        this.bids = bids;
    }

    public int getBidders() {
        return bidders;
    }

    public void setBidders(int bidders) {
        this.bidders = bidders;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getEnd_time_stamp() {
        return end_time_stamp;
    }

    public void setEnd_time_stamp(Long end_time_stamp) {
        this.end_time_stamp = end_time_stamp;
    }
}
