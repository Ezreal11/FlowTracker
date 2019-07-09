package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import edu.nju.ics.frontier.listener.MyCaretListener;
import edu.nju.ics.frontier.listener.MySelectionListener;

/**
 * <strong>MyCursorAgent</strong> manages the cursor listener,
 * which listens the cursor activities occurred in IDE's editors.
 */
public class MyCursorAgent implements Disposable {
    private EditorEventMulticaster multicaster;
    private CaretListener caretListener;
    private SelectionListener selectionListener;

    public MyCursorAgent() {
        multicaster = EditorFactory.getInstance().getEventMulticaster();

        caretListener = new MyCaretListener();
        selectionListener = new MySelectionListener();

        // register the caret listener through the editor event multicaster
        multicaster.addCaretListener(caretListener, this);
        // register the selection listener through the editor event multicaster
        multicaster.addSelectionListener(selectionListener, this);
    }

    @Override
    public void dispose() {
        // unregister the caret listener through the editor event multicaster
        this.multicaster.removeCaretListener(caretListener);
        // unregister the selection listener through the editor event multicaster
        this.multicaster.removeSelectionListener(selectionListener);
    }
}
