package edu.nju.ics.frontier.listener;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyDebuggerContextListener</strong> implements <em>DebuggerContextListener</em>
 * to listen the context of the debug activities.
 */
public class MyDebuggerContextListener extends MyListener implements DebuggerContextListener {
    public MyDebuggerContextListener(Persistence persistence) {
        super(persistence, "Debug");
    }

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
        String projectName = StringUtil.md5Encode(project.getName());
        String sessionName = StringUtil.md5Encode(session.getSessionName());
        String eventName = event.name();
        DebuggerSession.State state = session.getState();
        String stateName = (state == null) ? null : state.name();
        IntelliJEvent e = new IntelliJEvent(time, getType(), "context_changed");
        e.addExtraData("project", projectName);
        e.addExtraData("session", sessionName);
        e.addExtraData("event", eventName);
        e.addExtraData("state", stateName);
        getPersistence().add(e);
    }
}
