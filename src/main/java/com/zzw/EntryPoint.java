package com.zzw;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.zzw.compat.AppInfo;
import com.zzw.listener.debug.MyDebugAgent;
import com.zzw.listener.edit.MyEditAgent;
import com.zzw.listener.execute.MyExecuteAgent;
import com.zzw.listener.topic.MyTopicAgent;
import com.zzw.persist.Persistence;
import com.zzw.persist.Statistics;
import com.zzw.scale.control.LikertScale;
import com.zzw.tools.io.OkTextWriter;
import com.zzw.view.ScaleManager;
//import org.jetbrains.actionTracker.ActionTrackingService;
//import org.jetbrains.actionTracker.ActionTrackingServiceKt;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
//插件程序入口
public class EntryPoint implements ProjectComponent {
    private Project project;
    private MyDebugAgent debugAgent;
    private MyEditAgent editAgent;
    private MyExecuteAgent executeAgent;
    private MyTopicAgent topicAgent;
    private Persistence persistence;
    private Statistics statistics;
    private ScaleManager scaleManager;
    public static final OkTextWriter LOGGER = new OkTextWriter();

    //log信息位置——记录在C盘用户目录下
    static {
        String userHome = System.getProperty("user.home");
        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = userHome + File.separator + "interaction_traces" + File.separator +
                "plugin_log" + File.separator + dateStr + ".log";
        LOGGER.open(path);
        LOGGER.println(AppInfo.getAllVersion());
    }

    public EntryPoint(Project project) {
        LOGGER.println("EntryPoint constructor");
        this.project = project;
    }
    //初始化
    @Override
    public void initComponent() {
        LOGGER.println("initComponent");
        debugAgent = new MyDebugAgent(project);
        editAgent = new MyEditAgent(project);
        executeAgent = new MyExecuteAgent(project);
        topicAgent = new MyTopicAgent();
        persistence = Persistence.getInstance();
        statistics = Statistics.getInstance();
        scaleManager = ScaleManager.getInstance();
    }

    @Override
    public void disposeComponent() {
        LOGGER.println("disposeComponent");
        debugAgent = null;
        editAgent = null;
        executeAgent = null;
        topicAgent = null;
        persistence = null;
        statistics = null;
        scaleManager = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Action Tracker Control";
    }

    @Override
    public void projectOpened() {
//        controlActionTracker();
        persistence.execute();
        statistics.execute();
        statistics.add(System.currentTimeMillis(), Statistics.WINDOW_ACTIVATED);
        LOGGER.println("projectOpened");
    }

    @Override
    public void projectClosed() {
        Disposer.dispose(debugAgent);
        Disposer.dispose(editAgent);
        Disposer.dispose(executeAgent);
        Disposer.dispose(topicAgent);
        Disposer.dispose(scaleManager);

        String rootPath = System.getProperty("user.home") + File.separator +
                "interaction_traces" + File.separator + "scale";
        LikertScale scale = new LikertScale(rootPath, true);
        scale.addConfirmListener(Statistics.getInstance());
        scale.showLikertScaleDialog(false);

//        controlActionTracker();
        persistence.shutdown();
        statistics.shutdown();
        LOGGER.println("projectClosed");
    }

//    private void controlActionTracker() {
//        ActionTrackingService service = ActionTrackingServiceKt.getActionTrackingService(project);
//        if (service.getActiveTracker() != null) {
//            service.stopTracking();
//        } else {
//            service.startTracking();
//            Messages.showMessageDialog(
//                    project,
//                    "Start tracking",
//                    "Action Tracker",
//                    Messages.getInformationIcon());
//        }
//    }
}
