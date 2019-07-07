package com.zzw.listener.debug;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
//Debug事件监听器里 监听session相关事件的信息，包括session_created,session_attached,sessiondetached,session_removed
public class MyDebuggerManagerListener implements DebuggerManagerListener {
    private static final String TYPE = "Debug";

    @Override
    public void sessionCreated(DebuggerSession session) {
        onSessionChanged(System.currentTimeMillis(), "session_created", session);
    }

    @Override
    public void sessionAttached(DebuggerSession session) {
        onSessionChanged(System.currentTimeMillis(), "session_attached", session);
    }

    @Override
    public void sessionDetached(DebuggerSession session) {
        onSessionChanged(System.currentTimeMillis(), "session_detached", session);
    }

    @Override
    public void sessionRemoved(DebuggerSession session) {
        onSessionChanged(System.currentTimeMillis(), "session_removed", session);
    }

    private void onSessionChanged(long time, String when, DebuggerSession session) {
        Project project = (session == null) ? null : session.getProject();
        String projectName = (project == null) ? null : project.getName();
        String sessionName = (session == null) ? null : session.getSessionName();
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("project", projectName);
        e.addExtraData("session", sessionName);
        Persistence.getInstance().add(e);
    }
}
