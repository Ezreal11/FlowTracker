package edu.nju.ics.frontier.listener;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;

/**
 * <strong>MyDebuggerManagerListener</strong> implements <em>DebuggerManagerListener</em>
 * to listen the session of the debug activities.
 */
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

    /**
     * construct an <strong>IntelliJEvent</strong> for a debug event
     * and persist it in local file system.
     * the specific data for the debug IntelliJEvent consists:
     * <ul>
     *     <li>project: name of the project to be debugged (e.g., HelloWorld)</li>
     *     <li>session: nameof the debug session (e.g., Main)</li>
     * </ul>
     * @param time occurrence time of this debug event
     * @param when debug session changed
     * @param session debug session
     */
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
