package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import edu.nju.ics.frontier.persist.Persistence;

public abstract class MyAgent implements Disposable {
    protected Persistence persistence;

    public MyAgent(Persistence persistence) {
        this.persistence = persistence;
    }

    public Persistence getPersistence() {
        return persistence;
    }
}
