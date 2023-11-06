package com.namekart.amp2.GoDaddyEntities;

public class PurchaseResp {
    private String currency;
    private String code;

    public Long getItemCount() {
        return itemCount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getTotal() {
        return total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
    private Long itemCount;
    private Long orderId;

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    private Long total;
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
