package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import edu.nju.ics.frontier.listener.MyCompilationStatusListener;
import edu.nju.ics.frontier.persist.Persistence;

/**
 * <strong>MyCompileAgent</strong> manages the compile listener,
 * which listens the compile activities occurred in IDE.
 */
public class MyCompileAgent extends MyAgent {
    private CompilationStatusListener compilationStatusListener;

    public MyCompileAgent(Persistence persistence) {
        super(persistence);

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        // register the compile listener through CompilerManager
        this.compilationStatusListener = new MyCompilationStatusListener(persistence);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, compilationStatusListener);
        connection.deliverImmediately();

//        CompilerManager.getInstance(project).addCompilationStatusListener(compilationStatusListener, this);
    }

    @Override
    public void dispose() {
        // unregister the compile listener through CompilerManager
//        CompilerManager.getInstance(project).removeCompilationStatusListener(compilationStatusListener);
    }
}
