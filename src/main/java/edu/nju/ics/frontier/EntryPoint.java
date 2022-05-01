package edu.nju.ics.frontier;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import edu.nju.ics.frontier.scale.control.LikertScale;
import edu.nju.ics.frontier.util.OkTextWriter;
import edu.nju.ics.frontier.agent.*;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.IntelliJInfo;
import edu.nju.ics.frontier.view.RecManager;
import edu.nju.ics.frontier.view.ScaleManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * the entry point of this plugin, this plugin is enable if at least one project is opened.
 */
public class EntryPoint implements ProjectComponent {
    /**
     * current opened project
     */
    private Project project;

    /**
     * action agent to manage the action listener
     */
    private MyActionAgent actionAgent;

    /**
     * key, mouse and window agent to manage the key, mouse and window listener
     */
    private MyAWTEventAgent awtEventAgent;

    /**
     * compile agent to manage the compile listener
     */
    private MyCompileAgent compileAgent;

    /**
     * cursor agent to manage the cursor listener
     */
    private MyCursorAgent cursorAgent;

    /**
     * debug agent to manage the debug listener
     */
    private MyDebugAgent debugAgent;

    /**
     * execute agent to manage the execution listener
     */
    private MyExecuteAgent executeAgent;

    /**
     * file agent to manage the file and document listener
     */
    private MyFileAgent fileAgent;

    /**
     * persistence to persist the captured IntelliJ events
     */
    private Persistence persistence;

    /**
     * scale manager to manage the subjective scale
     */
    private ScaleManager scaleManager;
    //add
    private RecManager recmanager;
    /**
     * logger to monitor the status of this plugin
     */
    public static final OkTextWriter LOGGER = new OkTextWriter();

    /*
     * create and open the logger
     */
    static {
        String userHome = System.getProperty("user.home");
        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = userHome + File.separator + "interaction_traces" + File.separator +
                "plugin_log" + File.separator + dateStr + ".log";
        LOGGER.open(path);
        LOGGER.println(IntelliJInfo.getAllVersion());
    }

    /**
     * constructor
     * @param project created by IntelliJ IDEA
     */
    public EntryPoint(Project project) {
        LOGGER.println("EntryPoint constructor");
        this.project = project;
    }

    @Override
    public void initComponent() {
        LOGGER.println("initComponent");
        // create the agent instances
        actionAgent = new MyActionAgent();
        awtEventAgent = new MyAWTEventAgent();
        compileAgent = new MyCompileAgent(project);
        cursorAgent = new MyCursorAgent();
        debugAgent = new MyDebugAgent(project);
        executeAgent = new MyExecuteAgent();
        fileAgent = new MyFileAgent();
        persistence = Persistence.getInstance();
        scaleManager = ScaleManager.getInstance();
        //add
        recmanager = RecManager.getInstance();
    }

    @Override
    public void disposeComponent() {
        LOGGER.println("disposeComponent");
        // destroy the agent instances
        actionAgent = null;
        awtEventAgent = null;
        compileAgent = null;
        cursorAgent = null;
        debugAgent = null;
        executeAgent = null;
        fileAgent = null;
        persistence = null;
        scaleManager = null;
        //add
        recmanager=null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Interaction Activity Tracker";
    }

    @Override
    public void projectOpened() {
        // start the persistence
        persistence.execute();
        LOGGER.println("projectOpened");
    }

    @Override
    public void projectClosed() {
        // dispose agents
        Disposer.dispose(actionAgent);
        Disposer.dispose(awtEventAgent);
        Disposer.dispose(compileAgent);
        Disposer.dispose(cursorAgent);
        Disposer.dispose(debugAgent);
        Disposer.dispose(executeAgent);
        Disposer.dispose(fileAgent);
        Disposer.dispose(scaleManager);
        Disposer.dispose(recmanager);
        // show the subjective scale
        String rootPath = System.getProperty("user.home") + File.separator +
                "interaction_traces" + File.separator + "scale";
        LikertScale scale = new LikertScale(rootPath, true);
        scale.showLikertScaleDialog(false);

        // shutdown the persistence instance
        persistence.shutdown();
        LOGGER.println("projectClosed");
    }
}
