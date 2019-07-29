package edu.nju.ics.frontier.view;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.listener.MyAWTEventListener;
import edu.nju.ics.frontier.listener.MyApplicationActivationListener;
import edu.nju.ics.frontier.listener.MyExecutionListener;
import edu.nju.ics.frontier.util.OkTextReader;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ScaleManager implements LikertScaleDialog.ConfirmListener, MyExecutionListener.ExecutionActivationListener,
        MyAWTEventListener.ActivationListener, MyApplicationActivationListener.ActivationListener {
    private static final int WRITE = 1;
    private static final int GET_LAST_SHOW_TIME = 2;
    private static final int SET_LAST_SHOW_TIME = 3;

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
    private Gson gson = new Gson();

    private ConcurrentLinkedQueue<ScaleAction> queue = new ConcurrentLinkedQueue<>();
    private Semaphore queueSema = new Semaphore(0);
    private Semaphore querySema = new Semaphore(0);
    private Semaphore startSema = new Semaphore(0);
    private Semaphore stopSema = new Semaphore(0);
    private AtomicBoolean isClosed = new AtomicBoolean(true);
    private Thread ioThread;

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
     * private constructor
     */
    private ScaleManager() {}

    class ScaleAction {
        public final int act;
        public final String msg;

        public ScaleAction(int act, String msg) {
            this.act = act;
            this.msg = msg;
        }

    }

    class IOThread extends Thread {
        private String root = System.getProperty("user.home") + File.separator + "interaction_traces" + File.separator + "scale";
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        private OkTextWriter mWriter = new OkTextWriter();

        @Override
        public void run() {
            try {
                Date date = new Date();
                String dateStr = dateFormat.format(date);
                String timeStr = timeFormat.format(date);
                String path = root + File.separator + dateStr + File.separator +
                        timeStr + "_" + date.getTime() + ".json";
                mWriter.open(path);

                isClosed.set(false);
                startSema.release();

                while (!isInterrupted()) {
                    queueSema.acquire();
                    ScaleAction action = dequeue();
                    execute(action);
                }
            } catch (InterruptedException e) {
                // do nothing
            } finally {
                isClosed.set(false);
                for (ScaleAction action : queue) {
                    execute(action);
                }
                queue.clear();
                mWriter.close();
                stopSema.release();
            }
        }

        private void execute(ScaleAction action) {
            if (action == null) {
                return;
            }

            switch (action.act) {
                case WRITE: {
                    mWriter.println(action.msg);
                    break;
                }
                case GET_LAST_SHOW_TIME: {
                    File file = new File(root + File.separator + "scale_instance.txt");
                    if(file.exists()) {
                        OkTextReader reader = new OkTextReader();
                        reader.open(file);
                        String line = reader.readLine();
                        reader.close();
                        long time = Long.parseLong(line);
                        if (time > mLastShowTime.get()) {
                            mLastShowTime.set(time);
                        }
                    }
                    querySema.release();
                    break;
                }
                case SET_LAST_SHOW_TIME: {
                    OkTextWriter writer = new OkTextWriter();
                    writer.open(root + File.separator + "scale_instance.txt");
                    writer.println(System.currentTimeMillis());
                    writer.close();
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void startIOThread() {
        if (ioThread != null && ioThread.isAlive()) {
            return;
        }
        ioThread = new IOThread();
        ioThread.start();
        try {
            startSema.acquire();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    private void stopIOThread() {
        if (ioThread == null || ioThread.isInterrupted()) {
            return;
        }
        ioThread.interrupt();
        try {
            stopSema.acquire();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        ioThread = null;
    }

    public void execute() {
        startIOThread();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ScaleAction getAction = new ScaleAction(GET_LAST_SHOW_TIME, null);
                    enqueue(getAction);
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
        stopIOThread();
    }

    private synchronized void enqueue(ScaleAction action) {
        if (!(isClosed.get() || action == null)) {
            queue.add(action);
        }
    }

    private ScaleAction dequeue() {
        return queue.isEmpty() ? null : queue.poll();
    }

    @Override
    public void scaleOpened() {
        String msg = System.currentTimeMillis() + ",begin,open scale dialog";
        ScaleAction action = new ScaleAction(WRITE, msg);
        enqueue(action);
    }

    @Override
    public void refused(String noCoding) {
        String msg = System.currentTimeMillis() + ",refuse," + noCoding;
        ScaleAction action = new ScaleAction(WRITE, msg);
        enqueue(action);
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

        ScaleAction writeAction = new ScaleAction(WRITE, msg);
        enqueue(writeAction);
        ScaleAction setAction = new ScaleAction(SET_LAST_SHOW_TIME, null);
        enqueue(setAction);
        if (time > mLastShowTime.get()) {
            mLastShowTime.set(time);
        }
    }

    @Override
    public void scaleClosing() {
        String msg = System.currentTimeMillis() + ",end,close scale dialog";
        ScaleAction action = new ScaleAction(WRITE, msg);
        enqueue(action);
    }

    @Override
    public void answerChanged(AnswerModel model) {
        if (model == null) {
            return;
        }
        String json = gson.toJson(model);
        String msg = System.currentTimeMillis() + ",select," + json;
        ScaleAction action = new ScaleAction(WRITE, msg);
        enqueue(action);
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
    public void windowActivated(long time, WindowEvent e) {}

    @Override
    public void windowDeactivated(long time, WindowEvent e) {}

    @Override
    public void appActivated(long time) {
        mIsAppActivated.set(true);
    }

    @Override
    public void appDeactivated(long time) {
        mIsAppActivated.set(false);
    }

    @Override
    public void executionActivated(long time, int exitCode) {
        ScaleAction getAction = new ScaleAction(GET_LAST_SHOW_TIME, null);
        enqueue(getAction);
        show(time,
                false,
                false,
                true,
                true,
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
            dialog = null;
        }

        if (dialog != null && dialog.isVisible()) {
            return;
        }

        try {
            dialog = new LikertScaleDialog(modal);
            dialog.addConfirmListener(this);
            dialog.setVisible(true);
            dialog.toFront();
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            // java.lang.Throwable: AWT events are not allowed inside write action:
            // java.awt.event.InvocationEvent[INVOCATION_DEFAULT,runnable=sun.awt.
            // GlobalCursorManager$NativeUpdater@438b296e,notifier=null,catchExceptions=false,
            // when=1563671180359] on sun.awt.windows.WToolkit@4ec46a62
        }
    }
}
