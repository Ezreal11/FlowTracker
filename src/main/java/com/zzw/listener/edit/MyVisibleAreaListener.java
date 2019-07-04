package com.zzw.listener.edit;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MyVisibleAreaListener implements VisibleAreaListener {
    private static final String TYPE = "View";

    @Override
    public void visibleAreaChanged(@NotNull VisibleAreaEvent e) {
        long time = System.currentTimeMillis();
        Editor editor = (e == null) ? null : e.getEditor();
        EditorKind kind = (editor == null) ? null : editor.getEditorKind();
        String kindStr = (kind == null) ? null : kind.name();
        Rectangle oldRec = (e == null) ? null : e.getOldRectangle();
        Rectangle newRec = (e == null) ? null : e.getNewRectangle();
        String oldRecStr = (oldRec == null) ? null : ("[x=" + oldRec.x + ",y=" + oldRec.y + ",width=" + oldRec.width + ",height=" + oldRec.height + "]");
        String newRecStr = (newRec == null) ? null : ("[x=" + newRec.x + ",y=" + newRec.y + ",width=" + newRec.width + ",height=" + newRec.height + "]");
        if (!StringUtil.isDifferent(oldRecStr, newRecStr)) {
            return;
        }
        IntelliJEvent ie = new IntelliJEvent(time, TYPE, "changed");
        ie.addExtraData("editor", kindStr);
        ie.addExtraData("oldRec", oldRecStr);
        ie.addExtraData("newRec", newRecStr);
        Persistence.getInstance().add(ie);
    }
}
