package edu.nju.ics.frontier.view;

import com.google.gson.Gson;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DialogManager implements SubjectiveExperienceDialog.ConfirmListener {
    /**
     * the inactive time, 30 seconds by default
     */
    private static final long MAXIMUM_INACTIVATE_TIME = 2 * 60 * 1000;

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
     * last show time
     */
    private AtomicLong mLastShowTime = new AtomicLong(-1);

    /**
     * indicator of the application is activate or not
     */
    private AtomicBoolean mIsAppActivated = new AtomicBoolean(false);

    /**
     * timer to check whether the developer is active
     */
    private Timer timer;

    private SubjectiveExperienceDialog dialog;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
    private String root = System.getProperty("user.home") + File.separator + "interaction_traces" + File.separator + "scale";
    private OkTextWriter mWriter = new OkTextWriter();
    private Gson gson = new Gson();

    /**
     * the single instance of the 'DialogManager' class
     */
    private static DialogManager instance;

    /**
     * get the single instance of the 'DialogManager' class
     * @return the single instance of the 'DialogManager' class
     */
    public static DialogManager getInstance() {
        if (instance == null) {
            synchronized (DialogManager.class) {
                if (instance == null) {
                    instance = new DialogManager();
                }
            }
        }
        return instance;
    }

    /**
     * private constructor
     */
    private DialogManager() {}

    public void execute() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    show(System.currentTimeMillis(),
                            false,
                            false,
                            true,
                            true,
                            true);
                }
            }, MINIMUM_INTERVAL_TIME, MAXIMUM_INACTIVATE_TIME);
        }
    }

    public void shutdown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void scaleOpened() {
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String timeStr = timeFormat.format(date);
        String path = root + File.separator + dateStr + File.separator +
                timeStr + "_" + date.getTime() + ".json";
        mWriter.open(path);

        mWriter.println(System.currentTimeMillis() + ",begin,open scale dialog");

        long time = date.getTime();
        if (time > mLastShowTime.get()) {
            mLastShowTime.set(time);
        }
    }

    @Override
    public void scaleClosing() {
        long time = System.currentTimeMillis();
        mWriter.println(time + ",end,close scale dialog");
        mWriter.close();

        if (time > mLastShowTime.get()) {
            mLastShowTime.set(time);
        }
    }

    @Override
    public void answerChanged(AnswerModel model) {
        if (model == null) {
            return;
        }
        String json = gson.toJson(model);
        String msg = System.currentTimeMillis() + ",select," + json;
        mWriter.println(msg);
    }

    @Override
    public void refused(String noCoding) {
        String msg = System.currentTimeMillis() + ",refuse," + noCoding;
        mWriter.println(msg);
    }

    @Override
    public void confirmed(List<AnswerModel> models) {
        if (models == null || models.isEmpty()) {
            return;
        }

        long time = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < models.size(); i++) {
            String json = gson.toJson(models.get(i));
            builder.append(time).append(",submit,").append(json);
            if (i < models.size() - 1) {
                builder.append('\n');
            }
        }
        String msg = builder.toString();
        mWriter.println(msg);
    }

    public void keyOrMouseActivated(long time) {
        mLastActiveTime.set(time);
    }

    public void appActivated() {
        mIsAppActivated.set(true);
    }

    public void appDeactivated() {
        mIsAppActivated.set(false);
    }

    public void executionActivated(long time) {
        show(time,
                false,
                false,
                true,
                true,
                false);
    }

    public void allProjectExited(long time, Project project) {
//        show(time,
//                true,
//                false,
//                true,
//                false,
//                false);
    }

    /**
     * show the self-report dialog
     * @param time current time
     * @param modal {@code true} to block other windows when the dialog is showing
     * @param wait true to wait the dialog's thread finished
     * @param checkApp true to check whether the application is activate
     * @param checkInterval true to check whether the interval time is not less than 30 minutes
     * @param checkActivate true to check whether the keystroke activities are activate
     */
    public synchronized void show(long time, final boolean modal, boolean wait,
                                  boolean checkApp, boolean checkInterval, boolean checkActivate) {
        if (checkApp) {
            // check whether the application is activate
            if (!mIsAppActivated.get()) {
                return;
            }
        }

//        System.out.println("2");

        if (checkInterval) {
            // check whether the interval time is not less than 30 minutes
            long lastShowTime = mLastShowTime.get();
            if ((lastShowTime != -1) && ((time - lastShowTime) < MINIMUM_INTERVAL_TIME)) {
                return;
            }
        }

//        System.out.println("3");

        if (checkActivate) {
            // check whether the keystroke activities are activate
            long lastActiveTime = mLastActiveTime.get();
            if ((lastActiveTime != -1) && ((time - lastActiveTime) < MAXIMUM_INACTIVATE_TIME)) {
                return;
            }
        }

//        System.out.println("4");

        if (modal && dialog != null && (!dialog.isModal())) {
            dialog.dispose();
//            dialog.setVisible(false);
            dialog = null;
        }

//        System.out.println("5");

        if (dialog != null && dialog.isVisible()) {
            return;
        }

//        System.out.println("6");

        Application application = ApplicationManager.getApplication();
        if (application == null) {
            return;
        }

//        System.out.println("7");

        if (wait) {
            application.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    dialog = new SubjectiveExperienceDialog(true, modal);
                    dialog.addConfirmListener(DialogManager.this);
                    dialog.show();

//                    System.out.println("9");
                }
            });
        } else {
            application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog = new SubjectiveExperienceDialog(true, modal);
//                dialog = new SubjectiveExperienceDialog(project, ideModalityType);
                    dialog.addConfirmListener(DialogManager.this);
                    dialog.show();

//                    System.out.println("9");
                }
            });
        }

//        System.out.println("8");
    }
}
