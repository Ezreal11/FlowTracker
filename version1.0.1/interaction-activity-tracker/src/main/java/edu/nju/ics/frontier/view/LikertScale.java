package edu.nju.ics.frontier.view;

import com.google.gson.Gson;
import edu.nju.ics.frontier.Logger;
import edu.nju.ics.frontier.util.OkTextReader;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class LikertScale implements LikertScaleDialog.ConfirmListener {
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

    private LikertScaleDialog dialog;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
    private String root = System.getProperty("user.home") + File.separator + "interaction_traces" + File.separator + "scale";
    private OkTextWriter mWriter = new OkTextWriter();
    private Gson gson = new Gson();

    /**
     * the single instance of the 'LikertScale' class
     */
    private static LikertScale instance;

    /**
     * get the single instance of the 'LikertScale' class
     * @return the single instance of the 'LikertScale' class
     */
    public static LikertScale getInstance() {
        if (instance == null) {
            synchronized (LikertScale.class) {
                if (instance == null) {
                    instance = new LikertScale();
                }
            }
        }
        return instance;
    }

    /**
     * private constructor
     */
    private LikertScale() {}

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
            }, MINIMUM_INTERVAL_TIME, 300000);
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
    }

    @Override
    public void scaleClosing() {
        long time = System.currentTimeMillis();
        mWriter.println(time + ",end,close scale dialog");
        mWriter.close();

        if (time > mLastShowTime.get()) {
            mLastShowTime.set(time);
        }

//        OkTextWriter writer = new OkTextWriter();
//        writer.open(root + File.separator + "scale_instance.txt");
//        writer.println(System.currentTimeMillis());
//        writer.close();
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

    public void allProjectExited(long time) {
        show(time,
                true,
                true,
                true,
                false,
                false);
    }

    /**
     * show the self-report dialog
     * @param time current time
     * @param modal true to set the dialog to be modal
     * @param force true to reconstruct the dialog
     * @param checkApp true to check whether the application is activate
     * @param checkInterval true to check whether the interval time is not less than 30 minutes
     * @param checkActivate true to check whether the keystroke activities are activate
     */
    public synchronized void show(long time, boolean modal, boolean force,
                                  boolean checkApp, boolean checkInterval, boolean checkActivate) {
        if (checkApp) {
            // check whether the application is activate
            if (!mIsAppActivated.get()) {
                return;
            }
        }

        if (checkInterval) {
//            File file = new File(root + File.separator + "scale_instance.txt");
//            if(file.exists()) {
//                OkTextReader reader = new OkTextReader();
//                reader.open(file);
//                String line = reader.readLine();
//                reader.close();
//                long t = Long.parseLong(line);
//                if (t > mLastShowTime.get()) {
//                    mLastShowTime.set(t);
//                }
//            }

            // check whether the interval time is not less than 30 minutes
            long lastShowTime = mLastShowTime.get();
            if ((lastShowTime != -1) && ((time - lastShowTime) < MINIMUM_INTERVAL_TIME)) {
                return;
            }
        }

        if (checkActivate) {
            // check whether the keystroke activities are activate
            long lastActiveTime = mLastActiveTime.get();
            if ((lastActiveTime != -1) && ((time - lastActiveTime) < MAXIMUM_INACTIVATE_TIME)) {
                return;
            }
        }

        if (force && dialog != null) {
            dialog.dispose();
//            dialog.setVisible(false);
            dialog = null;
        }

        if (dialog != null && dialog.isVisible()) {
            return;
        }

        dialog = new LikertScaleDialog(modal);
        dialog.addConfirmListener(this);
        dialog.setVisible(true);
        dialog.toFront();
    }
}
