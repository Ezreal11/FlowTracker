package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import edu.nju.ics.frontier.listener.MyDocumentListener;
import edu.nju.ics.frontier.listener.MyFileEditorManagerListener;
import edu.nju.ics.frontier.listener.MyVirtualFileListener;

/**
 * <strong>MyFileAgent</strong> manages the file listener,
 * which listens the file activities occurred in IDE.
 */
public class MyFileAgent implements Disposable {
    private EditorEventMulticaster multicaster;
    private DocumentListener documentListener;
    private FileEditorManagerListener fileEditorManagerListener;
    private VirtualFileListener virtualFileListener;

    public MyFileAgent() {
        multicaster = EditorFactory.getInstance().getEventMulticaster();

        // register the document listener through editor event multicaster
        documentListener = new MyDocumentListener();
        multicaster.addDocumentListener(documentListener, this);

        // register the file editor manager listener through 'FileEditorManagerListener.FILE_EDITOR_MANAGER'
        fileEditorManagerListener = new MyFileEditorManagerListener();
        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorManagerListener);
        connection.deliverImmediately();

        // register the virtual file listener through 'VirtualFileManager'
        virtualFileListener = new MyVirtualFileListener();
        VirtualFileManager.getInstance().addVirtualFileListener(virtualFileListener, this);
    }

    @Override
    public void dispose() {
        // unregister the document listener through editor event multicaster
        this.multicaster.removeDocumentListener(documentListener);
    }
}
