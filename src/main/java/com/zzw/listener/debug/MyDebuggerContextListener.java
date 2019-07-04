package com.zzw.listener.debug;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

public class MyDebuggerContextListener implements DebuggerContextListener {
    private static final String TYPE = "Debug";

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl newContext, DebuggerSession.Event event) {
        long time = System.currentTimeMillis();
        if (newContext == null || event == null) {
            return;
        }
        Project project = newContext.getProject();
        if (project == null) {
            return;
        }
        DebuggerSession session = newContext.getDebuggerSession();
        if (session == null) {
            return;
        }
        String projectName = project.getName();
        String sessionName = session.getSessionName();
        String eventName = event.name();
        IntelliJEvent e = new IntelliJEvent(time, TYPE, "context_changed");
        e.addExtraData("project", projectName);
        e.addExtraData("session", sessionName);
        e.addExtraData("event", eventName);
        Persistence.getInstance().add(e);
    }
}
