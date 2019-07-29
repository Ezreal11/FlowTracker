package edu.nju.ics.frontier;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import edu.nju.ics.frontier.agent.*;
import edu.nju.ics.frontier.listener.MyAWTEventListener;
import edu.nju.ics.frontier.listener.MyApplicationActivationListener;
import edu.nju.ics.frontier.listener.MyExecutionListener;
import edu.nju.ics.frontier.listener.MyProjectManagerListener;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.view.DialogManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public class FlowTracker implements Disposable, MyApplicationActivationListener.ActivationListener, MyAWTEventListener.ActivationListener,
        MyExecutionListener.ExecutionActivationListener, MyProjectManagerListener.ProjectExitListener {
    private Persistence persistence;

    private MyActionAgent actionAgent;
    private MyAWTEventAgent awtEventAgent;
    private MyCompileAgent compileAgent;
    private MyCursorAgent cursorAgent;
//    private MyDebugAgent debugAgent;
    private MyExecuteAgent executeAgent;
    private MyFileAgent fileAgent;
    private MyProjectAgent projectAgent;

//    private LikertScale likertScale;
    private DialogManager dialogManager;

    private Logger logger = Logger.getInstance();

    public FlowTracker() {
        persistence = Persistence.getInstance();
        persistence.execute();

        actionAgent = new MyActionAgent(persistence);
        awtEventAgent = new MyAWTEventAgent(persistence);
        compileAgent = new MyCompileAgent(persistence);
        cursorAgent = new MyCursorAgent(persistence);
//        debugAgent = new MyDebugAgent(persistence);
        executeAgent = new MyExecuteAgent(persistence);
        fileAgent = new MyFileAgent(persistence);
        projectAgent = new MyProjectAgent(persistence);

        awtEventAgent.addAppActivationListener(this);
        awtEventAgent.addAWTActivationListener(this);
        executeAgent.addExecutionActivationListener(this);
        projectAgent.addProjectExitListener(this);

//        likertScale = LikertScale.getInstance();
//        likertScale.execute();
        dialogManager = DialogManager.getInstance();
        dialogManager.execute();

        logger.println(System.currentTimeMillis() + ",application started");
//        System.out.println("application started");
    }

    @Override
    public void dispose() {
        // the application is existed when this method over
        IntelliJEvent event = new IntelliJEvent(System.currentTimeMillis(), "App", "deactivated");
        persistence.add(event);

        dialogManager.show(System.currentTimeMillis(),
                true,
                true,
                true,
                false,
                false);

        Disposer.dispose(actionAgent);
        Disposer.dispose(awtEventAgent);
        Disposer.dispose(compileAgent);
        Disposer.dispose(cursorAgent);
//        Disposer.dispose(debugAgent);
        Disposer.dispose(executeAgent);
        Disposer.dispose(fileAgent);
        Disposer.dispose(projectAgent);

        persistence.shutdown();
//        likertScale.shutdown();
        dialogManager.shutdown();

        actionAgent = null;
        awtEventAgent = null;
        compileAgent = null;
        cursorAgent = null;
//        debugAgent = null;
        executeAgent = null;
        fileAgent = null;
        projectAgent = null;

        persistence = null;
//        likertScale = null;
        dialogManager = null;

        logger.println(System.currentTimeMillis() + ",application stopped");
//        System.out.println("application stopped");
    }

    @Override
    public void windowActivated(long time, WindowEvent e) {}

    @Override
    public void windowDeactivated(long time, WindowEvent e) {}

    private long lastActivatedTime = -1;

    private synchronized void keyOrMouseActivated(long time) {
        // the condition of 'time - lastActivatedTime > 1000'
        // is used to reduce the update frequency
        if (lastActivatedTime == -1 || (time - lastActivatedTime > 1000)) {
//            likertScale.keyOrMouseActivated(time);
            dialogManager.keyOrMouseActivated(time);
            lastActivatedTime = time;
//            System.out.println("key or mouse activated");
        }
    }

    @Override
    public void keyActivated(long time, KeyEvent e) {
        keyOrMouseActivated(time);
    }

    @Override
    public void mouseActivated(long time, MouseEvent e) {
        keyOrMouseActivated(time);
    }

    @Override
    public void appActivated(long time) {
//        likertScale.appActivated();
        dialogManager.appActivated();

//        System.out.println("appActivated");
    }

    @Override
    public void appDeactivated(long time) {
//        likertScale.appDeactivated();
        dialogManager.appDeactivated();

//        System.out.println("appDeactivated");
    }

    @Override
    public void executionActivated(long time, int exitCode) {
//        likertScale.executionActivated(time);
        dialogManager.executionActivated(time);

//        System.out.println("executionActivated");
    }

    @Override
    public void allProjectExited(long time, Project project) {
//        likertScale.allProjectExited(time);
//        dialogManager.allProjectExited(time, project);

        // but the application may not exited
//        System.out.println("allProjectExited");
    }
}
