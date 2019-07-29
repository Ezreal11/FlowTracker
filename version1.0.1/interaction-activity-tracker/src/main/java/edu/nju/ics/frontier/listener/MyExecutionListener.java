package edu.nju.ics.frontier.listener;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <strong>MyExecutionListener</strong> implements <em>ExecutionListener</em>
 * to listen the process of the execute activities.
 */
public class MyExecutionListener extends MyListener implements ExecutionListener {
    private final List<ExecutionActivationListener> listeners = new ArrayList<>();

    public void addExecutionActivationListener(ExecutionActivationListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
        }
    }

    public void removeExecutionActivationListener(ExecutionActivationListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public interface ExecutionActivationListener {
        void executionActivated(long time, int exitCode);
    }

    public MyExecutionListener(Persistence persistence) {
        super(persistence, "Execute");
    }

    @Override
    public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        onProcessChanged(System.currentTimeMillis(), "scheduled", executorId);
        if (!(listeners == null || listeners.isEmpty())) {
            long time = System.currentTimeMillis();
            for (ExecutionActivationListener l : listeners) {
                l.executionActivated(time, 0);
            }
        }
    }

    @Override
    public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        onProcessChanged(System.currentTimeMillis(), "starting", executorId);
    }

    @Override
    public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        onProcessChanged(System.currentTimeMillis(), "cancelled", executorId);
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        onProcessChanged(System.currentTimeMillis(), "started", executorId);
    }

    @Override
    public void processTerminating(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        onProcessChanged(System.currentTimeMillis(), "terminating", executorId);
    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        onProcessChanged(System.currentTimeMillis(), "terminated", executorId);
//        if (!(listeners == null || listeners.isEmpty())) {
//            long time = System.currentTimeMillis();
//            for (ExecutionActivationListener l : listeners) {
//                l.executionActivated(time, exitCode);
//            }
//        }
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a execute event
     * and persist it in local file system.
     * the specific data for the execute IntelliJEvent consists:
     * <ul>
     *     <li>id: id of the executor (e.g., Debug)</li>
     * </ul>
     * @param time occurrence time of this debug event
     * @param when debug session changed
     * @param executorId id of the executor
     */
    private void onProcessChanged(long time, String when, String executorId) {
        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
        e.addExtraData("id", executorId);
        getPersistence().add(e);
    }
}
