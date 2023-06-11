package com.namekart.amp2.NamecheapEntity;

public class NCEvent {
    String type;

    NCEventData data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NCEventData getData() {
        return data;
    }

    public void setData(NCEventData data) {
        this.data = data;
    }
}
