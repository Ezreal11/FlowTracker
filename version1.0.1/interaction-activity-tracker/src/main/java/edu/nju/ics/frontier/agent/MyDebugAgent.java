package edu.nju.ics.frontier.agent;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerStateManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import edu.nju.ics.frontier.listener.MyDebuggerContextListener;
import edu.nju.ics.frontier.listener.MyDebuggerManagerListener;
import edu.nju.ics.frontier.listener.MyXBreakpointListener;
import edu.nju.ics.frontier.persist.Persistence;

/**
 * <strong>MyDebugAgent</strong> manages the debug listener,
 * which listens the debug activities occurred in IDE.
 */
public class MyDebugAgent extends MyAgent {
//    private DebuggerContextListener debuggerContextListener;
//    private DebuggerManagerListener debuggerManagerListener;
    private XBreakpointListener breakpointListener;

    public MyDebugAgent(Persistence persistence) {
        super(persistence);

//        this.debuggerContextListener = new MyDebuggerContextListener(persistence);
//        this.debuggerManagerListener = new MyDebuggerManagerListener(persistence);

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        // register the break point listener through 'XDebuggerManager'
        this.breakpointListener = new MyXBreakpointListener(persistence);
        connection.subscribe(XBreakpointListener.TOPIC, breakpointListener);
        connection.deliverImmediately();

//        XDebuggerManager.getInstance(project).
//                getBreakpointManager().addBreakpointListener(breakpointListener);

        // register the debugger manager listener through 'DebuggerManagerEx'
//        DebuggerManagerEx.getInstanceEx(project).
//                addDebuggerManagerListener(debuggerManagerListener);
        // register the debugger context listener through 'DebuggerManagerEx'
//        DebuggerManagerEx.getInstanceEx(project).
//                getContextManager().addListener(debuggerContextListener);
    }

    @Override
    public void dispose() {
        // unregister the debugger manager listener through 'DebuggerManagerEx'
//        DebuggerManagerEx.getInstanceEx(this.project).
//                removeDebuggerManagerListener(debuggerManagerListener);
        // unregister the debugger context listener through 'DebuggerManagerEx'
//        DebuggerManagerEx.getInstanceEx(this.project).
//                getContextManager().removeListener(debuggerContextListener);
        // unregister the break point listener through 'XDebuggerManager'
//        XDebuggerManager.getInstance(this.project).
//                getBreakpointManager().removeBreakpointListener(breakpointListener);
    }
}
