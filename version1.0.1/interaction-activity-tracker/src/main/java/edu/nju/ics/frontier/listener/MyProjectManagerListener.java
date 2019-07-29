package edu.nju.ics.frontier.listener;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyProjectManagerListener extends MyListener implements ProjectManagerListener {
    private ConcurrentHashMap<Project, DebuggerListeners>
            debuggerListenersMap = new ConcurrentHashMap<>();

    private List<ProjectExitListener> listeners = new ArrayList<>();

    public void addProjectExitListener(ProjectExitListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
        }
    }

    public void removeProjectExitListener(ProjectExitListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public interface ProjectExitListener {
        void allProjectExited(long time, Project project);
    }

    public MyProjectManagerListener(Persistence persistence) {
        super(persistence, "Project");
    }

    @Override
    public synchronized void projectOpened(@NotNull Project project) {
        Window window = (project == null) ? null : WindowManager.getInstance().suggestParentWindow(project);
        String windowName = (window == null) ? null : window.getName();

        IntelliJEvent event = new IntelliJEvent(System.currentTimeMillis(), getType(), "opened");
        event.addExtraData("name", StringUtil.md5Encode(project.getName()));
        event.addExtraData("frame", windowName);
        getPersistence().add(event);

        registerListeners(project);

//        System.out.println("project [" + project.getName() + "] opened");
    }

    @Override
    public synchronized void projectClosed(@NotNull Project project) {
        Window window = (project == null) ? null : WindowManager.getInstance().suggestParentWindow(project);
        String windowName = (window == null) ? null : window.getName();

        IntelliJEvent event = new IntelliJEvent(System.currentTimeMillis(), getType(), "closed");
        event.addExtraData("name", StringUtil.md5Encode(project.getName()));
        event.addExtraData("frame", windowName);
        getPersistence().add(event);

        unregisterListeners(project);

        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects == null || projects.length <= 0 || debuggerListenersMap.isEmpty()) {
            if (!listeners.isEmpty()) {
                long time = System.currentTimeMillis();
                for (ProjectExitListener l : listeners) {
                    l.allProjectExited(time, project);
                }
            }
        }

//        System.out.println("project [" + project.getName() + "] closed");
    }

    class DebuggerListeners {
        XBreakpointListener breakpointListener;
        DebuggerContextListener debuggerContextListener;

        public DebuggerListeners(XBreakpointListener breakpointListener, DebuggerContextListener debuggerContextListener) {
            this.breakpointListener = breakpointListener;
            this.debuggerContextListener = debuggerContextListener;
        }

        public XBreakpointListener getBreakpointListener() {
            return breakpointListener;
        }

        public DebuggerContextListener getDebuggerContextListener() {
            return debuggerContextListener;
        }
    }

    private void registerListeners(Project project) {
        if (project == null || debuggerListenersMap.containsKey(project)) {
            return;
        }

        // register the break point listener through 'XDebuggerManager'
        XBreakpointListener breakpointListener = new MyXBreakpointListener(getPersistence());
        XDebuggerManager.getInstance(project).
                getBreakpointManager().addBreakpointListener(breakpointListener);

        // register the debugger context listener through 'DebuggerManagerEx'
        DebuggerContextListener debuggerContextListener = new MyDebuggerContextListener(getPersistence());
        DebuggerManagerEx.getInstanceEx(project).
                getContextManager().addListener(debuggerContextListener);

        DebuggerListeners listeners = new DebuggerListeners(breakpointListener, debuggerContextListener);
        debuggerListenersMap.put(project, listeners);
    }

    private void unregisterListeners(Project project) {
        if (project == null || (!debuggerListenersMap.containsKey(project))) {
            return;
        }

        DebuggerListeners listeners = debuggerListenersMap.get(project);

        // unregister the break point listener through 'XDebuggerManager'
        XDebuggerManager.getInstance(project).
                getBreakpointManager().removeBreakpointListener(listeners.breakpointListener);

        // unregister the debugger context listener through 'DebuggerManagerEx'
        DebuggerManagerEx.getInstanceEx(project).
                getContextManager().removeListener(listeners.debuggerContextListener);
        debuggerListenersMap.remove(project);
    }
}
