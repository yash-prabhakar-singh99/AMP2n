package com.namekart.amp2.Entity;

import java.util.ArrayList;
import java.util.List;

public class BulkScheduleResponse {
    List<Integer> l;

    public BulkScheduleResponse() {
    }

    public BulkScheduleResponse(List<Integer> l, String str) {
        this.l = l;
        this.str = str;
    }

    public List<Integer> getL() {
        return l;
    }

    public void setL(List<Integer> l) {
        this.l = l;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    String str;
}
