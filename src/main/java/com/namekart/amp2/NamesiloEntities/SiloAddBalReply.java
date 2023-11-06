package com.namekart.amp2.NamesiloEntities;

public class SiloAddBalReply {
    int code;
    String detail;
    Float new_balance;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Float getNew_balance() {
        return new_balance;
    }

    public void setNew_balance(Float new_balance) {
        this.new_balance = new_balance;
    }
}
