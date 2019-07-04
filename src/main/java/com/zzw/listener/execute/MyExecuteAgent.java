package com.zzw.listener.execute;

import com.intellij.execution.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.zzw.view.ScaleManager;

public class MyExecuteAgent implements Disposable {
    private Project project;
    private CompilationStatusListener compilationStatusListener;
    private MyExecutionListener executionListener;
    private ExecutionTargetListener executionTargetListener;

    public MyExecuteAgent(Project project) {
        this.project = project;
        this.compilationStatusListener = new MyCompilationStatusListener();
        this.executionListener = new MyExecutionListener();
        this.executionListener.addExecutionActivationListener(ScaleManager.getInstance());
        this.executionTargetListener = new MyExecutionTargetListener();

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        CompilerManager.getInstance(this.project).addCompilationStatusListener(compilationStatusListener, this);
        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, executionListener);
        connection.subscribe(ExecutionTargetManager.TOPIC, executionTargetListener);
    }

    @Override
    public void dispose() {
        CompilerManager.getInstance(project).removeCompilationStatusListener(compilationStatusListener);
    }
}
