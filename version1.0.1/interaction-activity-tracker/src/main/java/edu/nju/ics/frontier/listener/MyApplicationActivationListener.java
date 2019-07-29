package edu.nju.ics.frontier.listener;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.WindowManagerImpl;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyApplicationActivationListener extends MyListener implements ApplicationActivationListener {
    private List<ActivationListener> listeners = new ArrayList<>();

    public void addActivationListener(ActivationListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
        }
    }

    public void removeActivationListener(ActivationListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public interface ActivationListener {
        void appActivated(long time);
        void appDeactivated(long time);
    }

    public MyApplicationActivationListener(Persistence persistence) {
        super(persistence, "App");
    }

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
//        System.out.println("applicationActivated");

        long time = System.currentTimeMillis();
        IntelliJEvent event = new IntelliJEvent(time, getType(), "activated");
        getPersistence().add(event);

        if (!listeners.isEmpty()) {
            for (ActivationListener l : listeners) {
                l.appActivated(time);
            }
        }
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
//        System.out.println("applicationDeactivated");

        long time = System.currentTimeMillis();
        IntelliJEvent event = new IntelliJEvent(time, getType(), "deactivated");
        getPersistence().add(event);

        if (!listeners.isEmpty()) {
            for (ActivationListener l : listeners) {
                l.appDeactivated(time);
            }
        }
    }

    @Override
    public void delayedApplicationDeactivated(@NotNull IdeFrame ideFrame) {
//        System.out.println("delayedApplicationDeactivated");
    }
}
