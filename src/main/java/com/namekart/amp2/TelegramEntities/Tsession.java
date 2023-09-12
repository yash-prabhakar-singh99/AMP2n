package com.namekart.amp2.TelegramEntities;

public class Tsession {
    int permission;
    String prevCmd;
    String platform;
    String data;

    public Tsession(int permission, String prevCmd, String platform, String data) {
        this.permission = permission;
        this.prevCmd = prevCmd;
        this.platform = platform;
        this.data = data;
    }

    public Tsession() {
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getPrevCmd() {
        return prevCmd;
    }

    public void setPrevCmd(String prevCmd) {
        this.prevCmd = prevCmd;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
