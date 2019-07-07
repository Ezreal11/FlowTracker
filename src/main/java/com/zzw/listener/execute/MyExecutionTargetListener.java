package com.zzw.listener.execute;

import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.ExecutionTargetListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

//Execute记录模块，记录每个Active_target_changed事件
public class MyExecutionTargetListener implements ExecutionTargetListener {
    private static final String TYPE = "Execute";

    @Override
    public void activeTargetChanged(@NotNull ExecutionTarget newTarget) {
        long time = System.currentTimeMillis();
        String id = (newTarget == null) ? null : newTarget.getId();
        String name = (newTarget == null) ? null : newTarget.getDisplayName();
        IntelliJEvent e = new IntelliJEvent(time, TYPE, "active_target_changed");
        e.addExtraData("id", id);
        e.addExtraData("name", name);
        Persistence.getInstance().add(e);
    }
}
