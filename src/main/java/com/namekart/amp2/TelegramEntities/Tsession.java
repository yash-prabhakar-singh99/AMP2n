package com.namekart.amp2.TelegramEntities;

import java.util.HashSet;
import java.util.Set;

public class Tsession {
    int permission;
    String prevCmd;
    String platform;
    String data;

    Set<String> roles=new HashSet<>();

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Tsession(Set<String> roles) {
        this.roles = roles;
    }

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
