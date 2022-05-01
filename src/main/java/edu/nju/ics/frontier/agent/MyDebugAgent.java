package edu.nju.ics.frontier.agent;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import edu.nju.ics.frontier.listener.MyDebuggerContextListener;
import edu.nju.ics.frontier.listener.MyDebuggerManagerListener;
import edu.nju.ics.frontier.listener.MyXBreakpointListener;

/**
 * <strong>MyDebugAgent</strong> manages the debug listener,
 * which listens the debug activities occurred in IDE.
 */
public class MyDebugAgent implements Disposable {
    private Project project;
    private DebuggerContextListener debuggerContextListener;
    private DebuggerManagerListener debuggerManagerListener;
    private XBreakpointListener breakpointListener;

    public MyDebugAgent(Project project) {
        this.project = project;
        this.debuggerContextListener = new MyDebuggerContextListener();
        this.debuggerManagerListener = new MyDebuggerManagerListener();
        this.breakpointListener = new MyXBreakpointListener();

        // register the debugger manager listener through 'DebuggerManagerEx'
        DebuggerManagerEx.getInstanceEx(this.project).
                addDebuggerManagerListener(debuggerManagerListener);
        // register the debugger context listener through 'DebuggerManagerEx'
        DebuggerManagerEx.getInstanceEx(this.project).
                getContextManager().addListener(debuggerContextListener);
        // register the break point listener through 'XDebuggerManager'
        XDebuggerManager.getInstance(this.project).
                getBreakpointManager().addBreakpointListener(breakpointListener);
    }

    @Override
    public void dispose() {
        // unregister the debugger manager listener through 'DebuggerManagerEx'
        DebuggerManagerEx.getInstanceEx(this.project).
                removeDebuggerManagerListener(debuggerManagerListener);
        // unregister the debugger context listener through 'DebuggerManagerEx'
        DebuggerManagerEx.getInstanceEx(this.project).
                getContextManager().removeListener(debuggerContextListener);
        // unregister the break point listener through 'XDebuggerManager'
        XDebuggerManager.getInstance(this.project).
                getBreakpointManager().removeBreakpointListener(breakpointListener);
    }
}
