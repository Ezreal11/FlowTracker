package com.zzw.persist;

import java.util.HashMap;
import java.util.Map;

public class IntelliJEvent {
    private long time;
    private String type;
    private String when;
    private Map<String, String> data;

    public IntelliJEvent(long time, String type, String when) {
        this.time = time;
        this.type = type;
        this.when = when;
    }

    public void addExtraData(String k, String v) {
        if (k != null && v != null) {
            if (data == null) {
                data = new HashMap<>();
            }
            data.put(k, v);
        }
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getWhen() {
        return when;
    }

    public Map<String, String> getData() {
        return data;
    }
}
