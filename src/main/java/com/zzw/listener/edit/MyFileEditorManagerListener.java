package com.zzw.listener.edit;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;
//文件(File)监视器,当文件被打开、关闭时触发
public class MyFileEditorManagerListener implements FileEditorManagerListener {
    private static final String TYPE = "File";

    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {}

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        onFileChanged(System.currentTimeMillis(), "opened", file);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        onFileChanged(System.currentTimeMillis(), "closed", file);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        long time = System.currentTimeMillis();
        VirtualFile oldFile = (event == null) ? null : event.getOldFile();
        VirtualFile newFile = (event == null) ? null : event.getNewFile();
        IntelliJEvent e = new IntelliJEvent(time, TYPE, "selected");
        e.addExtraData("oldPath", VirtualFileUtil.getPath(oldFile));
        e.addExtraData("newPath", VirtualFileUtil.getPath(newFile));
        Persistence.getInstance().add(e);
    }

    private void onFileChanged(long time, String when, VirtualFile file) {
        Document document = VirtualFileUtil.getDocument(file);

        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("docLen", VirtualFileUtil.getDocumentLengthString(document));
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));
        Persistence.getInstance().add(e);
    }
}
