package edu.nju.ics.frontier.listener;

import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.ExecutionTargetListener;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyExecutionListener</strong> implements <em>ExecutionListener</em>
 * to listen the target of the execute activities.
 */
public class MyExecutionTargetListener extends MyListener implements ExecutionTargetListener {
    public MyExecutionTargetListener(Persistence persistence) {
        super(persistence, "Execute");
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a execute event
     * and persist it in local file system.
     * the specific data for the execute IntelliJEvent consists:
     * <ul>
     *     <li>id: id of the executor (e.g., Debug)</li>
     *     <li>name: display name of the target</li>
     * </ul>
     * @see ExecutionTargetListener#activeTargetChanged(ExecutionTarget)
     * @param newTarget new target
     */
    @Override
    public void activeTargetChanged(@NotNull ExecutionTarget newTarget) {
        long time = System.currentTimeMillis();
        String id = (newTarget == null) ? null : newTarget.getId();
        String name = (newTarget == null) ? null : newTarget.getDisplayName();
        IntelliJEvent e = new IntelliJEvent(time, getType(), "active_target_changed");
        e.addExtraData("id", id);
        e.addExtraData("name", name);
        getPersistence().add(e);
    }
}
