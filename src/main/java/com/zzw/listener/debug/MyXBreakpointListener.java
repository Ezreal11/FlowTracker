package com.zzw.listener.debug;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

public class MyXBreakpointListener implements XBreakpointListener<XBreakpoint<?>> {
    private static final String TYPE = "Debug";

    @Override
    public void breakpointAdded(@NotNull XBreakpoint<?> breakpoint) {
        onBreakpointChanged(System.currentTimeMillis(), "breakpoint_added");
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint<?> breakpoint) {
        onBreakpointChanged(System.currentTimeMillis(), "breakpoint_removed");
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint<?> breakpoint) {
        onBreakpointChanged(System.currentTimeMillis(), "breakpoint_changed");
    }

    private void onBreakpointChanged(long time, String when) {
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        Persistence.getInstance().add(e);
    }
}
