package com.zzw.listener.debug;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
//Debug事件监视器管理器，管理监听到的下面三大类debug事件
public class MyDebugAgent implements Disposable {
    private Project project;
    private MyDebuggerContextListener debuggerContextListener;
    private MyDebuggerManagerListener debuggerManagerListener;
    private MyXBreakpointListener breakpointListener;

    public MyDebugAgent(Project project) {
        this.project = project;
        this.debuggerContextListener = new MyDebuggerContextListener();
        this.debuggerManagerListener = new MyDebuggerManagerListener();
        this.breakpointListener = new MyXBreakpointListener();

        DebuggerManagerEx.getInstanceEx(this.project).
                addDebuggerManagerListener(debuggerManagerListener);
        DebuggerManagerEx.getInstanceEx(this.project).
                getContextManager().addListener(debuggerContextListener);
        XDebuggerManager.getInstance(this.project).
                getBreakpointManager().addBreakpointListener(breakpointListener);
    }

    @Override
    public void dispose() {
        DebuggerManagerEx.getInstanceEx(this.project).
                removeDebuggerManagerListener(debuggerManagerListener);
        DebuggerManagerEx.getInstanceEx(this.project).
                getContextManager().removeListener(debuggerContextListener);
        XDebuggerManager.getInstance(this.project).
                getBreakpointManager().removeBreakpointListener(breakpointListener);
    }
}
