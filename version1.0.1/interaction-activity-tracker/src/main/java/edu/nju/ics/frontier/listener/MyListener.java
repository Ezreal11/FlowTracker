package edu.nju.ics.frontier.listener;

import edu.nju.ics.frontier.persist.Persistence;

public abstract class MyListener {
    protected Persistence persistence;
    protected String type;

    public MyListener(Persistence persistence, String type) {
        this.persistence = persistence;
        this.type = type;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public String getType() {
        return type;
    }
}
