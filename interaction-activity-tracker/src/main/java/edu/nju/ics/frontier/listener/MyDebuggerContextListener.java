package edu.nju.ics.frontier.listener;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyDebuggerContextListener</strong> implements <em>DebuggerContextListener</em>
 * to listen the context of the debug activities.
 */
public class MyDebuggerContextListener implements DebuggerContextListener {
    private static final String TYPE = "Debug";

    /**
     * the specific data for the cursor IntelliJEvent consists:
     * <ul>
     *     <li>project: name of the project to be debugged (e.g., HelloWorld)</li>
     *     <li>session: nameof the debug session (e.g., Main)</li>
     *     <li>event: name of the debug event (e.g., CONTEXT)</li>
     * </ul>
     * @see DebuggerContextListener#changeEvent(DebuggerContextImpl, DebuggerSession.Event)
     * @param newContext new context
     * @param event debug event
     */
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
