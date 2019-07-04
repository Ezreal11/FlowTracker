package com.zzw.listener.edit;

import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

public class MyEditorMouseListener implements EditorMouseListener, EditorMouseMotionListener {
    private static final String TYPE = "Edit";

    @Override
    public void mousePressed(@NotNull EditorMouseEvent event) {
        onMouseChanged(System.currentTimeMillis(), "mouse_pressed", event);
    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        onMouseChanged(System.currentTimeMillis(), "mouse_clicked", event);
    }

    @Override
    public void mouseReleased(@NotNull EditorMouseEvent event) {
        onMouseChanged(System.currentTimeMillis(), "mouse_released", event);
    }

    @Override
    public void mouseEntered(@NotNull EditorMouseEvent event) {
        onMouseChanged(System.currentTimeMillis(), "mouse_entered", event);
    }

    @Override
    public void mouseExited(@NotNull EditorMouseEvent event) {
        onMouseChanged(System.currentTimeMillis(), "mouse_exited", event);
    }

    @Override
    public void mouseMoved(@NotNull EditorMouseEvent e) {
        onMouseChanged(System.currentTimeMillis(), "mouse_moved", e);
    }

    @Override
    public void mouseDragged(@NotNull EditorMouseEvent e) {
        onMouseChanged(System.currentTimeMillis(), "mouse_dragged", e);
    }

    private void onMouseChanged(long time, String when, EditorMouseEvent e) {
        MouseEvent mouseEvent = (e == null) ? null : e.getMouseEvent();
        String info = (mouseEvent == null) ? null : mouseEvent.paramString();
        IntelliJEvent ie = new IntelliJEvent(time, TYPE, when);
        ie.addExtraData("info", info);
        Persistence.getInstance().add(ie);
    }
}
