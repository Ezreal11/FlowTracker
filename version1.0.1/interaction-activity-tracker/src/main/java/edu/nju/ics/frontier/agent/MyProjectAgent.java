package edu.nju.ics.frontier.agent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import edu.nju.ics.frontier.listener.MyProjectManagerListener;
import edu.nju.ics.frontier.persist.Persistence;

public class MyProjectAgent extends MyAgent {
    private MyProjectManagerListener projectManagerListener;

    public MyProjectAgent(Persistence persistence) {
        super(persistence);

        MessageBusConnection connection = ApplicationManager.getApplication().
                getMessageBus().connect();
        Disposer.register(this, connection);

        // register the project listener
        projectManagerListener = new MyProjectManagerListener(persistence);
        connection.subscribe(ProjectManager.TOPIC, projectManagerListener);
        connection.deliverImmediately();
    }

    @Override
    public void dispose() {}

    public void addProjectExitListener(MyProjectManagerListener.ProjectExitListener l) {
        projectManagerListener.addProjectExitListener(l);
    }

    public void removeProjectExitListener(MyProjectManagerListener.ProjectExitListener l) {
        projectManagerListener.removeProjectExitListener(l);
    }
}
