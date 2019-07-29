package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import edu.nju.ics.frontier.listener.MyAWTEventListener;
import edu.nju.ics.frontier.listener.MyApplicationActivationListener;
import edu.nju.ics.frontier.persist.Persistence;

/**
 * <strong>MyAWTEventAgent</strong> manages the key, mouse and window listeners,
 * which listens the key, mouse and window activities triggered in IDE and omits
 * the activities out of IDE.
 */
public class MyAWTEventAgent extends MyAgent {
    private MyAWTEventListener awtEventListener;
    private MyApplicationActivationListener applicationActivationListener;

    public MyAWTEventAgent(Persistence persistence) {
        super(persistence);

        awtEventListener = new MyAWTEventListener(persistence);
        Disposer.register(this, awtEventListener);

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        // register the execution listener through 'ExecutionManager.EXECUTION_TOPIC'
        applicationActivationListener = new MyApplicationActivationListener(persistence);
        connection.subscribe(ApplicationActivationListener.TOPIC, applicationActivationListener);
        connection.deliverImmediately();
    }

    @Override
    public void dispose() {}

    public void addAWTActivationListener(MyAWTEventListener.ActivationListener l) {
        awtEventListener.addActivationListener(l);
    }

    public void removeAWTActivationListener(MyAWTEventListener.ActivationListener l) {
        awtEventListener.removeActivationListener(l);
    }

    public void addAppActivationListener(MyApplicationActivationListener.ActivationListener l) {
        applicationActivationListener.addActivationListener(l);
    }

    public void removeAppActivationListener(MyApplicationActivationListener.ActivationListener l) {
        applicationActivationListener.removeActivationListener(l);
    }
}
