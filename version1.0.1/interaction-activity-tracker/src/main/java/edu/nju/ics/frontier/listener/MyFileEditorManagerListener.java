package edu.nju.ics.frontier.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyFileEditorManagerListener</strong> implements <em>FileEditorManagerListener</em>
 * to listen the status of the files in IDE.
 */
public class MyFileEditorManagerListener extends MyListener implements FileEditorManagerListener {
    public MyFileEditorManagerListener(Persistence persistence) {
        super(persistence, "File");
    }

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
        IntelliJEvent e = new IntelliJEvent(time, getType(), "selected");
        e.addExtraData("oldPath", VirtualFileUtil.getPath(oldFile));
        e.addExtraData("newPath", VirtualFileUtil.getPath(newFile));
        getPersistence().add(e);
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a file event
     * and persist it in local file system.
     * the specific data for the file IntelliJEvent consists:
     * <ul>
     *     <li>path: MD5-coded path of the document</li>
     *     <li>fileLen: length of the corresponding file of the document</li>
     *     <li>docLen: length of the document</li>
     *     <li>line: line of code in the document</li>
     * </ul>
     * @param time occurrence time of this event
     * @param when when file status changed
     * @param file the virtual file
     */
    private void onFileChanged(long time, String when, VirtualFile file) {
        Document document = VirtualFileUtil.getDocument(file);

        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("docLen", VirtualFileUtil.getDocumentLengthString(document));
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));
        getPersistence().add(e);
    }
}
