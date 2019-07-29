package edu.nju.ics.frontier.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyCaretListener</strong> implements <em>CaretListener</em> to listen the
 * cursor activities.
 */
public class MyCaretListener extends MyListener implements CaretListener {
    public MyCaretListener(Persistence persistence) {
        super(persistence, "Cursor");
    }

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {
        onCaretChanged(System.currentTimeMillis(), "cursor_changed", event);
    }

    @Override
    public void caretAdded(@NotNull CaretEvent event) {
        onCaretChanged(System.currentTimeMillis(), "cursor_added", event);
    }

    @Override
    public void caretRemoved(@NotNull CaretEvent event) {
        onCaretChanged(System.currentTimeMillis(), "cursor_removed", event);
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a cursor event
     * and persist it in local file system.
     * the specific data for the cursor IntelliJEvent consists:
     * <ul>
     *     <li>editor: type of editor the cursor is in (e.g., CONSOLE)</li>
     *     <li>oldPos: position in the editor before the cursor moved (e.g., (0,0))</li>
     *     <li>newPos: position in the editor after the cursor moved (e.g., (1,0))</li>
     * </ul>
     * @param time occurrence time of this action
     * @param when when action performed
     * @param event the cursor event
     */
    private void onCaretChanged(long time, String when, CaretEvent event) {
        Editor editor = (event == null) ? null : event.getEditor();
        EditorKind kind = (editor == null) ? null : editor.getEditorKind();
        String kindStr = (kind == null) ? null : kind.name();
        LogicalPosition oldPos = (event == null) ? null : event.getOldPosition();
        LogicalPosition newPos = (event == null) ? null : event.getNewPosition();
        String oldPosStr = (oldPos == null) ? null : ("(" + oldPos.line + "," + oldPos.column + ")");
        String newPosStr = (newPos == null) ? null : ("(" + newPos.line + "," + newPos.column + ")");
        if (!StringUtil.isDifferent(oldPosStr, newPosStr)) {
            return;
        }
        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
        e.addExtraData("editor", kindStr);
        e.addExtraData("oldPos", oldPosStr);
        e.addExtraData("newPos", newPosStr);
        getPersistence().add(e);
    }
}
