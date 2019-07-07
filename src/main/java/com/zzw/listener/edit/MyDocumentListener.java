package com.zzw.listener.edit;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

//监视文档是否被修改的事件，同时记录修改前后的文档属性，例如修改行数、文档长度等
public class MyDocumentListener implements DocumentListener {
    private static final String TYPE = "File";

    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
//        onDocumentChanged(System.currentTimeMillis(), "before_change", event);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        onDocumentChanged(System.currentTimeMillis(), "changed", event);
    }

    private void onDocumentChanged(long time, String when, DocumentEvent event) {
        Document document = (event == null) ? null : event.getDocument();
        int oldLen = (event == null) ? -1 : event.getOldLength();
        int newLen = (event == null) ? -1 : event.getNewLength();
        if (oldLen == newLen) {
            return;
        }
        String oldLenStr = (oldLen == -1) ? null : String.valueOf(oldLen);
        String newLenStr = (newLen == -1) ? null : String.valueOf(newLen);

        VirtualFile file = VirtualFileUtil.getFile(document);

        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("oldDocLen", oldLenStr);
        e.addExtraData("newDocLen", newLenStr);
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));
        Persistence.getInstance().add(e);
    }
}
