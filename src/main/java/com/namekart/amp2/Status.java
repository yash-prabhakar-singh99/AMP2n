package com.namekart.amp2;

import java.util.concurrent.ScheduledFuture;

public class Status {
    ScheduledFuture future;
    String futureTask;
    boolean account;

    public boolean isQuick() {
        return quick;
    }

    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public Status(ScheduledFuture future, String futureTask, boolean account, boolean quick) {
        this.future = future;
        this.futureTask = futureTask;
        this.account = account;
        this.quick = quick;
    }

    boolean quick;

    public Status(ScheduledFuture future, String futureTask, boolean account) {
        this.future = future;
        this.futureTask = futureTask;
        this.account = account;
    }

    public boolean isAccount() {
        return account;
    }

    public void setAccount(boolean account) {
        this.account = account;
    }

    public Status(ScheduledFuture future, String futureTask) {
        this.future = future;
        this.futureTask = futureTask;
    }

    public ScheduledFuture getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    public String getFutureTask() {
        return futureTask;
    }

    public void setFutureTask(String futureTask) {
        this.futureTask = futureTask;
    }
}
