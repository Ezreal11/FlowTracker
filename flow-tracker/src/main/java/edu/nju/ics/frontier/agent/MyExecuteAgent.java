package edu.nju.ics.frontier.agent;

import com.intellij.execution.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import edu.nju.ics.frontier.listener.MyExecutionListener;
import edu.nju.ics.frontier.listener.MyExecutionTargetListener;
import edu.nju.ics.frontier.view.ScaleManager;

/**
 * <strong>MyExecuteAgent</strong> manages the execute listener,
 * which listens the execute activities occurred in IDE.
 */
public class MyExecuteAgent implements Disposable {
    private MyExecutionListener executionListener;
    private ExecutionTargetListener executionTargetListener;

    public MyExecuteAgent() {
        this.executionListener = new MyExecutionListener();
        this.executionListener.addExecutionActivationListener(ScaleManager.getInstance());
        this.executionTargetListener = new MyExecutionTargetListener();

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        // dispose this message bus connection when 'MyExecuteAgent' is disposed
        Disposer.register(this, connection);

        // register the execution listener through 'ExecutionManager.EXECUTION_TOPIC'
        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, executionListener);
        connection.deliverImmediately();
        // register the execution target listener through 'ExecutionTargetManager.TOPIC'
        connection.subscribe(ExecutionTargetManager.TOPIC, executionTargetListener);
        connection.deliverImmediately();
    }

    @Override
    public void dispose() {}
}
