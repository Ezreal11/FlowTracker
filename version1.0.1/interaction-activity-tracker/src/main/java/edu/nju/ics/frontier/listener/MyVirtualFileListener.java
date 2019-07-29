package edu.nju.ics.frontier.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.VirtualFileUtil;
import edu.nju.ics.frontier.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyVirtualFileListener</strong> implements <em>VirtualFileListener</em>
 * to listen the operations for the files in IDE.
 */
public class MyVirtualFileListener extends MyListener implements VirtualFileListener {
    public MyVirtualFileListener(Persistence persistence) {
        super(persistence, "File");
    }

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

    /**
     *
     * construct an <strong>IntelliJEvent</strong> for a file event
     * and persist it in local file system.
     * the specific data for the file IntelliJEvent consists:
     * <ul>
     *     <li>path: MD5-coded path of the document</li>
     *     <li>fileLen: length of the corresponding file of the document</li>
     *     <li>docLen: length of the document</li>
     *     <li>line: line of code in the document</li>
     *     <li>property: [specific data for <em>VirtualFilePropertyEvent</em>] name of the property changed</li>
     *     <li>oldValue: [specific data for <em>VirtualFilePropertyEvent</em>] old value of the property</li>
     *     <li>newValue: [specific data for <em>VirtualFilePropertyEvent</em>] new value of the property</li>
     *     <li>oldParent: [specific data for <em>VirtualFileMoveEvent</em>] MD5-coded name of the parent of the file before file moved</li>
     *     <li>newParent: [specific data for <em>VirtualFileMoveEvent</em>] MD5-coded name of the parent of the file after file moved</li>
     *     <li>original: [specific data for <em>VirtualFileCopyEvent</em>] MD5-coded name of the original file</li>
     * </ul>
     * @param time occurrence time of this event
     * @param when when new operation occurred
     * @param event the virtual file event
     */
    private void onFileChanged(long time, String when, @NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Document document = VirtualFileUtil.getDocument(file);
        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
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

        getPersistence().add(e);
    }
}
