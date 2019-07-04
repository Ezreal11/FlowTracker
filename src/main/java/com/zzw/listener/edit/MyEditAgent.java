package com.zzw.listener.edit;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.util.messages.MessageBusConnection;
import com.zzw.view.ScaleManager;

public class MyEditAgent implements Disposable {
    private Project project;
    private EditorEventMulticaster multicaster;
    private CaretListener caretListener;
    private DocumentListener documentListener;
//    private MyEditorMouseListener editorMouseListener;
    private FileEditorManagerListener fileEditorManagerListener;
    private MyKeystrokeListener keystrokeListener;
    private SelectionListener selectionListener;
//    private PsiTreeChangeListener psiTreeChangeListener;
    private VirtualFileListener virtualFileListener;
//    private FileDocumentManagerListener fileDocumentManagerListener;
//    private VisibleAreaListener visibleAreaListener;

    public MyEditAgent(Project project) {
        this.project = project;

        multicaster = EditorFactory.getInstance().getEventMulticaster();

        caretListener = new MyCaretListener();
        documentListener = new MyDocumentListener();
//        editorMouseListener = new MyEditorMouseListener();
        fileEditorManagerListener = new MyFileEditorManagerListener();
        selectionListener = new MySelectionListener();
//        psiTreeChangeListener = new MyPsiTreeChangeListener();
        virtualFileListener = new MyVirtualFileListener();

        multicaster.addCaretListener(caretListener, this);
        multicaster.addDocumentListener(documentListener, this);
//        multicaster.addEditorMouseListener(editorMouseListener, this);
//        multicaster.addEditorMouseMotionListener(editorMouseListener, this);
        multicaster.addSelectionListener(selectionListener, this);

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorManagerListener);

        keystrokeListener = new MyKeystrokeListener();
        keystrokeListener.addKeyActivationListener(ScaleManager.getInstance());
        Disposer.register(this, keystrokeListener);

//        PsiManager.getInstance(project).addPsiTreeChangeListener(psiTreeChangeListener, this);
        VirtualFileManager.getInstance().addVirtualFileListener(virtualFileListener, this);

//        fileDocumentManagerListener = new MyFileDocumentManagerListener();
//        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, fileDocumentManagerListener);
//        visibleAreaListener = new MyVisibleAreaListener();
//        this.multicaster.addVisibleAreaListener(visibleAreaListener);
    }

    @Override
    public void dispose() {
        this.multicaster.removeCaretListener(caretListener);
        this.multicaster.removeDocumentListener(documentListener);
//        this.multicaster.removeEditorMouseListener(editorMouseListener);
//        this.multicaster.removeEditorMouseMotionListener(editorMouseListener);
        this.multicaster.removeSelectionListener(selectionListener);
//        this.multicaster.removeVisibleAreaListener(visibleAreaListener);
    }
}
