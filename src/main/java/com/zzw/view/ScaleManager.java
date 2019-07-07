package com.zzw.view;

import com.intellij.openapi.Disposable;
import com.zzw.listener.edit.MyKeystrokeListener;
import com.zzw.listener.execute.MyExecutionListener;
import com.zzw.persist.Statistics;
import com.zzw.scale.control.LikertScale;
import com.zzw.scale.model.AnswerModel;
import com.zzw.scale.view.LikertScaleDialog;
import com.zzw.tools.io.OkTextReader;
import com.zzw.tools.io.OkTextWriter;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

//问卷模块,当每次填完问卷将填写时间记录在本地，固定时长以后再弹出问卷
public class ScaleManager implements Disposable,
        MyKeystrokeListener.KeyActivationListener,
        MyExecutionListener.ExecutionActivationListener,
        LikertScaleDialog.ConfirmListener {
    private static final long MAXIMUM_INACTIVATE_TIME = 30 * 1000;      // 30 seconds
    private static final long MINIMUM_INTERVAL_TIME = 60 * 60 * 1000;   // 60 minutes
    private AtomicLong mLastActiveTime = new AtomicLong(-1);
//    private AtomicLong mLastShowTime = new AtomicLong(-1);
    private AtomicBoolean mIsWinActivated = new AtomicBoolean(false);
    private Timer timer = new Timer();
    private String rootPath = System.getProperty("user.home") + File.separator +
            "interaction_traces" + File.separator + "scale";
    private LikertScale scale;

//    private Semaphore initSemaphore = new Semaphore(0); // for thread initialization
//    private Semaphore showSemaphore = new Semaphore(0); // for scale show
//    private Semaphore exitSemaphore = new Semaphore(0); // for scale exit
//    private Thread thread;  // thread to open/close scale

    private static ScaleManager instance;

//    private class ScaleThread extends Thread {
//        private String rootPath;
//        private LikertScale scale;
//
//        public ScaleThread() {
//            rootPath = System.getProperty("user.home") + File.separator +
//                    "interaction_traces" + File.separator + "scale";
//        }
//
//        @Override
//        public void run() {
            // this thread is running
//            initSemaphore.release();
//
//            try {
//                while (!isInterrupted()) {
                    // block until an new require arrived
//                    showSemaphore.acquire();
//                    showScale();
                    // block until the scale closed
//                    exitSemaphore.acquire();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//        private void showScale() {
//            if (scale != null && scale.isLikertScaleDialogVisible()) {
//                 scale already opened
//                return;
//            }
//            scale = new LikertScale(rootPath);
//            scale.addConfirmListener(ScaleManager.this);
//            scale.addConfirmListener(Statistics.getInstance());
//            scale.showLikertScaleDialog(false);
//        }
//    }

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

    private ScaleManager() {
//        thread = new ScaleThread();
//        thread.start();
//        try {
//            // guarantee the thread is running
//            initSemaphore.acquire();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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
    }

    public synchronized void show(long time) {
        boolean isWinActivated = mIsWinActivated.get();
        if (!isWinActivated) {
            // intellij idea is not activated
            return;
        }

        //long lastShowTime = mLastShowTime.get();

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

//        showSemaphore.release();

        if (scale != null && scale.isLikertScaleDialogVisible()) {
            // scale already opened
            return;
        }
        scale = new LikertScale(rootPath, false);
        scale.addConfirmListener(ScaleManager.this);
        scale.addConfirmListener(Statistics.getInstance());
        scale.showLikertScaleDialog(false);
    }

    @Override
    public void dispose() {
        timer.cancel();
//        thread.interrupt();
    }

    @Override
    public void keyActivated(long time, KeyEvent e) {
//        System.out.println(time + "," + e.paramString());
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED || id == KeyEvent.KEY_TYPED) {
            mLastActiveTime.set(time);
        }
    }

    @Override
    public void mouseActivated(long time, MouseEvent e) {}

    @Override
    public void windowActivated(long time, WindowEvent e) {
//        System.out.println(time + ",windowActivated," + e.paramString());
        mIsWinActivated.set(true);
    }

    @Override
    public void windowDeactivated(long time, WindowEvent e) {
//        System.out.println(time + ",windowDeactivated," + e.paramString());
        mIsWinActivated.set(false);
    }

    @Override
    public void executionActivated(long time, int exitCode) {
//        System.out.println(time + "," + exitCode);
        show(time);
    }

    @Override
    public void scaleOpened() {}

    @Override
    public void confirmed(List<AnswerModel> list) {
        //mLastShowTime.set(System.currentTimeMillis());

        OkTextWriter writer = new OkTextWriter();
        writer.open(rootPath + File.separator + "scale_instance.txt");
        writer.println(System.currentTimeMillis());
        writer.close();
    }

    @Override
    public void scaleClosing() {
//        exitSemaphore.release();
    }
}
