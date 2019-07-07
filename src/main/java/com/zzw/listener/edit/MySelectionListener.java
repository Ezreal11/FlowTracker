package com.zzw.listener.edit;

import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.util.TextRange;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.StringUtil;
import org.jetbrains.annotations.NotNull;
//光标(Cursor)事件监视器，包括选择事件
public class MySelectionListener implements SelectionListener {
    private static final String TYPE = "Cursor";

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
        IntelliJEvent ie = new IntelliJEvent(time, TYPE, "select_changed");
        ie.addExtraData("old", oldRangeStr);
        ie.addExtraData("new", newRangeStr);
        Persistence.getInstance().add(ie);
    }
}
