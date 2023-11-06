package com.namekart.amp2.SettingsEntity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FastBidSetting {
    @Id
    String platform;

    int fastN,fastBidAmount;

    public FastBidSetting(String platform, int fastN, int fastBidAmount) {
        this.platform = platform;
        this.fastN = fastN;
        this.fastBidAmount = fastBidAmount;
    }

    public FastBidSetting() {
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getFastN() {
        return fastN;
    }

    public void setFastN(int fastN) {
        this.fastN = fastN;
    }

    public int getFastBidAmount() {
        return fastBidAmount;
    }

    public void setFastBidAmount(int fastBidAmount) {
        this.fastBidAmount = fastBidAmount;
    }
}
