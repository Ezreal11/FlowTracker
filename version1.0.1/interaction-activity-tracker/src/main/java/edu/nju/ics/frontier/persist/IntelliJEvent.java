package edu.nju.ics.frontier.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is created to donate an IntelliJ event, which consists of
 * an event, the occurrence time of this event, the type of this event,
 * and option extra data.
 */
public class IntelliJEvent {
    private long time;                  // the occurrence time of this event
    private String type;                // the type of this event
    private String when;                // the event
    private Map<String, String> data;   // option extra data

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{time:").append(time).
                append(",type:").append(type).
                append(",when:").append(when);
        if (!(data == null || data.isEmpty())) {
            builder.append(",data:{");
            List<String> keyList = new ArrayList<>(data.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                String value = data.get(key);
                builder.append(key).append(":").append(value);
                if (i < keyList.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("}");
        }
        builder.append("}");
        return builder.toString();
    }
}
