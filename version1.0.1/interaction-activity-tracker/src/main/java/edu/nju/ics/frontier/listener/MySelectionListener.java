package edu.nju.ics.frontier.listener;

import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MySelectionListener</strong> implements <em>SelectionListener</em>
 * to listen the selection activities.
 */
public class MySelectionListener extends MyListener implements SelectionListener {
    public MySelectionListener(Persistence persistence) {
        super(persistence, "Cursor");
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a cursor event
     * and persist it in local file system.
     * the specific data for the cursor selection IntelliJEvent consists:
     * <ul>
     *     <li>old: selection range before the cursor selected (e.g., (486,489))</li>
     *     <li>new: selection range after the cursor selected (e.g., (486,488))</li>
     * </ul>
     * @see SelectionListener#selectionChanged(SelectionEvent)
     * @param e
     */
    @Override
    public void selectionChanged(@NotNull SelectionEvent e) {
        long time = System.currentTimeMillis();
        TextRange oldRange = (e == null) ? null : e.getOldRange();
        TextRange newRange = (e == null) ? null : e.getNewRange();
        String oldRangeStr = (oldRange == null) ? null : oldRange.toString();
        String newRangeStr = (newRange == null) ? null : newRange.toString();
        if (!StringUtil.isDifferent(oldRangeStr, newRangeStr)) {
            return;
        }
        IntelliJEvent ie = new IntelliJEvent(time, getType(), "select_changed");
        ie.addExtraData("old", oldRangeStr);
        ie.addExtraData("new", newRangeStr);
        getPersistence().add(ie);
    }
}
