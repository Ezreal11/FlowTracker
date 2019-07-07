package com.zzw.listener.edit;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;
//文件(File)监视器,当文件被加载，保存等情况时触发
public class MyFileDocumentManagerListener implements FileDocumentManagerListener {
    private static final String TYPE = "File";

    @Override
    public void beforeAllDocumentsSaving() {}

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {}

    @Override
    public void beforeFileContentReload(@NotNull VirtualFile file, @NotNull Document document) {}

    @Override
    public void fileWithNoDocumentChanged(@NotNull VirtualFile file) {}

    @Override
    public void fileContentReloaded(@NotNull VirtualFile file, @NotNull Document document) {
        onFileContentChanged(System.currentTimeMillis(), "reloaded", file, document);
    }

    @Override
    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {
        onFileContentChanged(System.currentTimeMillis(), "loaded", file, document);
    }

    @Override
    public void unsavedDocumentsDropped() {}

    private void onFileContentChanged(long time, String when, VirtualFile file, Document document) {
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));
        e.addExtraData("docLen", VirtualFileUtil.getDocumentLengthString(document));
        Persistence.getInstance().add(e);
    }
}
