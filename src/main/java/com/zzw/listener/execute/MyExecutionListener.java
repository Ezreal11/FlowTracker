package com.zzw.listener.execute;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyExecutionListener implements ExecutionListener {
    private static final String TYPE = "Execute";
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

    private void onProcessChanged(long time, String when, String executorId) {
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("id", executorId);
        Persistence.getInstance().add(e);
    }
}
