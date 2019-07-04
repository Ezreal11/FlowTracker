package com.zzw.listener.edit;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class MyCaretListener implements CaretListener {
    private static final String TYPE = "Cursor";

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
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("editor", kindStr);
        e.addExtraData("oldPos", oldPosStr);
        e.addExtraData("newPos", newPosStr);
        Persistence.getInstance().add(e);
    }
}
