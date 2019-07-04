package com.zzw.listener.edit;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.*;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.StringUtil;
import com.zzw.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

public class MyVirtualFileListener implements VirtualFileListener {
    private static final String TYPE = "File";

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        onFileChanged(System.currentTimeMillis(), "property_changed", event);
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        onFileChanged(System.currentTimeMillis(), "content_changed", event);
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        onFileChanged(System.currentTimeMillis(), "created", event);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        onFileChanged(System.currentTimeMillis(), "deleted", event);
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        onFileChanged(System.currentTimeMillis(), "moved", event);
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        onFileChanged(System.currentTimeMillis(), "copied", event);
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {}

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {}

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {}

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {}

    private void onFileChanged(long time, String when, @NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Document document = VirtualFileUtil.getDocument(file);
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("docLen", VirtualFileUtil.getDocumentLengthString(document));
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));

        if (event instanceof VirtualFilePropertyEvent) {
            VirtualFilePropertyEvent pe = (VirtualFilePropertyEvent) event;
            String property = pe.getPropertyName();
            Object oldValue = pe.getOldValue();
            Object newValue = pe.getNewValue();
            if (StringUtil.isDifferent(oldValue, newValue)) {
                String oldValueStr = (oldValue == null) ? null : oldValue.toString();
                String newValueStr = (newValue == null) ? null : newValue.toString();
                e.addExtraData("property", property);
                e.addExtraData("oldValue", oldValueStr);
                e.addExtraData("newValue", newValueStr);
            }
        }

        if (event instanceof VirtualFileMoveEvent) {
            VirtualFileMoveEvent me = (VirtualFileMoveEvent) event;
            VirtualFile oldParent = me.getOldParent();
            VirtualFile newParent = me.getNewParent();
            e.addExtraData("oldParent", VirtualFileUtil.getPath(oldParent));
            e.addExtraData("newParent", VirtualFileUtil.getPath(newParent));
        }

        if (event instanceof VirtualFileCopyEvent) {
            VirtualFileCopyEvent ce = (VirtualFileCopyEvent) event;
            VirtualFile original = ce.getOriginalFile();
            e.addExtraData("original", VirtualFileUtil.getPath(original));
        }

        Persistence.getInstance().add(e);
    }
}
