package com.namekart.amp2.EstibotEntity;

import java.util.List;

public class Estibot_Results {
    public int total;
    public int count;
    public List<Estibot_Data> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Estibot_Data> getData() {
        return data;
    }

    public void setData(List<Estibot_Data> data) {
        this.data = data;
    }
}
