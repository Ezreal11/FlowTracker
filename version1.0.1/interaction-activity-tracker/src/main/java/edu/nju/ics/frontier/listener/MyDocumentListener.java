package edu.nju.ics.frontier.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

/**
 * <strong>MyDocumentListener</strong> implements <em>DocumentListener</em>
 * to listen the change of the documents.
 */
public class MyDocumentListener extends MyListener implements DocumentListener {
    public MyDocumentListener(Persistence persistence) {
        super(persistence, "File");
    }

    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
//        onDocumentChanged(System.currentTimeMillis(), "before_change", event);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        onDocumentChanged(System.currentTimeMillis(), "changed", event);
    }

    /**
     * construct an <strong>IntelliJEvent</strong> for a document event
     * and persist it in local file system.
     * the specific data for the document IntelliJEvent consists:
     * <ul>
     *     <li>path: MD5-coded path of the document</li>
     *     <li>fileLen: length of the corresponding file of the document</li>
     *     <li>oldDocLen: length of the document before document changed</li>
     *     <li>newDocLen: length of the document after document changed</li>
     *     <li>line: line of code in the document</li>
     * </ul>
     * @param time occurrence time of this event
     * @param when when document changed
     * @param event the document event
     */
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

        IntelliJEvent e = new IntelliJEvent(time, getType(), when);
        e.addExtraData("path", VirtualFileUtil.getPath(file));
        e.addExtraData("fileLen", VirtualFileUtil.getFileLengthString(file));
        e.addExtraData("oldDocLen", oldLenStr);
        e.addExtraData("newDocLen", newLenStr);
        e.addExtraData("line", VirtualFileUtil.getDocumentLineString(document));
        getPersistence().add(e);
    }
}
