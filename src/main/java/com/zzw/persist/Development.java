package com.zzw.persist;
//处理活跃时间和非活跃时间
public class Development {
    private long activatedTime;     // time indicates the IntelliJ IDEA is activated
    private long deactivatedTime;   // time indicates the IntelliJ IDEA is deactivated

    public Development(long activatedTime, long deactivatedTime) {
        this.activatedTime = activatedTime;
        this.deactivatedTime = deactivatedTime;
    }

    public long getActivatedTime() {
        return activatedTime;
    }

    public long getDeactivatedTime() {
        return deactivatedTime;
    }

    @Override
    public String toString() {
        return String.format("{activatedTime:%d,deactivatedTime:%d}",
                activatedTime, deactivatedTime);
    }
}
