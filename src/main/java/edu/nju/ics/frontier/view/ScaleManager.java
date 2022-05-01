package edu.nju.ics.frontier.view;

import com.intellij.openapi.Disposable;
import edu.nju.ics.frontier.scale.control.LikertScale;
import edu.nju.ics.frontier.scale.model.AnswerModel;
import edu.nju.ics.frontier.scale.view.LikertScaleDialog;
import edu.nju.ics.frontier.util.OkTextReader;
import edu.nju.ics.frontier.util.OkTextWriter;
import edu.nju.ics.frontier.listener.MyAWTEventListener;
import edu.nju.ics.frontier.listener.MyExecutionListener;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <strong>ScaleManager</strong> manages the subjective scale (questionnaire).
 * It shows the subjective scale dialog when developers are inactive
 * (no keystroke in 30 seconds), or a 'run'/'debug' activity started.
 * The subjective scale dialog doesn't appear if the IDE is not at foreground,
 * the the time interval between two appearance of the subjective scale dialog
 * is not less than 30 minutes.
 */
public class ScaleManager implements Disposable,
        MyAWTEventListener.ActivationListener,
        MyExecutionListener.ExecutionActivationListener,
        LikertScaleDialog.ConfirmListener {
    /**
     * the inactive time, 30 seconds by default
     */
    private static final long MAXIMUM_INACTIVATE_TIME = 30 * 1000;

    /**
     * the time interval between two appearance of the subjective scale dialog,
     * 30 minutes by default
     */
    private static final long MINIMUM_INTERVAL_TIME = 30 * 60 * 1000;

    /**
     * last active time
     */
    private AtomicLong mLastActiveTime = new AtomicLong(-1);

    /**
     * indicator of the window is at foreground or not
     */
    private AtomicBoolean mIsWinActivated = new AtomicBoolean(false);

    /**
     * timer to check whether the developer is active
     */
    private Timer timer = new Timer();

    private Timer rectimer=new Timer();
    /**
     * directory to place the submitted questionnaires
     */
    private String rootPath = System.getProperty("user.home") + File.separator +
            "interaction_traces" + File.separator + "scale";

    /**
     * the instance of the subjective scale
     */
    private LikertScale scale;

    /**
     * the single instance of the 'ScaleManager' class
     */
    private static ScaleManager instance;

    /**
     * get the single instance of the 'ScaleManager' class
     * @return the single instance of the 'ScaleManager' class
     */
    public static ScaleManager getInstance() {
        if (instance == null) {
            synchronized (ScaleManager.class) {
                if (instance == null) {
                    instance = new ScaleManager();
                }
            }
        }
        return instance;
    }

    /**
     * Constructor, the timer also starts.
     */
    private ScaleManager() {
        System.out.println("in ScaleManager constructor");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currTime = System.currentTimeMillis();
                long lastActiveTime = mLastActiveTime.get();
                if ((lastActiveTime != -1) && (currTime - lastActiveTime < MAXIMUM_INACTIVATE_TIME)) {
                    return;
                }
                show(currTime);
            }
        }, MINIMUM_INTERVAL_TIME, 300000);


        //add
        /*rectimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("in rectimer!!");
            }
        },0,10000);*/

    }

    /**
     * show the subjective scale
     * @param time current time
     */
    public synchronized void show(long time) {
        boolean isWinActivated = mIsWinActivated.get();
        if (!isWinActivated) {
            // intellij idea is not at foreground
            return;
        }

        /*
         * Because there are multiple IDEs are opened at the same time and plugins are
         * independent in each IDE, we use a mark to record the last submitted time of
         * the subjective scale to avoid filling the scale repeatedly in a short time.
         */
        File file = new File(rootPath + File.separator + "scale_instance.txt");
        if(file.exists()){
            OkTextReader reader = new OkTextReader();
            reader.open(file);
            String line = reader.readLine();
            reader.close();
            long lastShowTime = Long.parseLong(line);
            if ((time - lastShowTime) < MINIMUM_INTERVAL_TIME) {
                return;
            }
        }

        if (scale != null && scale.isLikertScaleDialogVisible()) {
            // scale already opened
            return;
        }
        scale = new LikertScale(rootPath, false);
        scale.addConfirmListener(ScaleManager.this);
        scale.showLikertScaleDialog(false);
    }

    /**
     * stop the timer
     * @see Disposable#dispose()
     */
    @Override
    public void dispose() {
        timer.cancel();
    }

    @Override
    public void keyActivated(long time, KeyEvent e) {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED || id == KeyEvent.KEY_TYPED) {
            mLastActiveTime.set(time);
        }
    }

    @Override
    public void mouseActivated(long time, MouseEvent e) {}

    @Override
    public void windowActivated(long time, WindowEvent e) {
        mIsWinActivated.set(true);
    }

    @Override
    public void windowDeactivated(long time, WindowEvent e) {
        mIsWinActivated.set(false);
    }

    @Override
    public void executionActivated(long time, int exitCode) {
        show(time);
    }

    @Override
    public void scaleOpened() {}

    @Override
    public void confirmed(List<AnswerModel> list) {
        // update the mark when a subjective scale is submitted
        OkTextWriter writer = new OkTextWriter();
        writer.open(rootPath + File.separator + "scale_instance.txt");
        writer.println(System.currentTimeMillis());
        writer.close();
    }

    @Override
    public void scaleClosing() {}
}
