package edu.nju.ics.frontier.listener;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyXBreakpointListener</strong> implements <em>XBreakpointListener</em>
 * to listen the break point of the debug activities.
 */
public class MyXBreakpointListener extends MyListener implements XBreakpointListener<XBreakpoint<?>> {
    public MyXBreakpointListener(Persistence persistence) {
        super(persistence, "Debug");
    }

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

    /**
     * construct an <strong>IntelliJEvent</strong> for a debug event
     * and persist it in local file system.
     * No specific data for the break point debug IntelliJEvent.
     * @param time occurrence time of this debug event
     * @param when debug session changed
     */
    private void onBreakpointChanged(long time, String when) {
        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
        getPersistence().add(e);
    }
}
