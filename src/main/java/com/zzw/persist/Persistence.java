package com.zzw.persist;

import com.google.gson.Gson;
import com.zzw.EntryPoint;
import com.zzw.tools.io.OkTextWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

//持久层，保证数据采集工具一直运行
public class Persistence {
    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
    private LinkedList<IntelliJEvent> queue = new LinkedList<>();
    private Semaphore queueSema = new Semaphore(0);
    private Semaphore threadStartSema = new Semaphore(0);
    private Semaphore threadStopSema = new Semaphore(0);
    private OkTextWriter writer = new OkTextWriter();
    private Gson gson = new Gson();
    private boolean isClosed = true;
    private Thread thread;
    private Timer timer;
    private static Persistence instance;

    class MyThread extends Thread {
        @Override
        public void run() {
            try {
                threadStartSema.release();
                while (!isInterrupted()) {
                    queueSema.acquire();
                    IntelliJEvent event = remove();
                    if (event != null) {
                        writer.println(gson.toJson(event));
                    }
                }
            } catch (InterruptedException e) {
                // shutdown this thread
            } finally {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    IntelliJEvent event = queue.get(i);
                    if (event != null) {
                        writer.println(gson.toJson(event));
                    }
                }
                queue.clear();
                writer.close();
                threadStopSema.release();
            }
        }
    }

    public static Persistence getInstance() {
        if (instance == null) {
            synchronized (Persistence.class) {
                if (instance == null) {
                    instance = new Persistence();
                }
            }
        }
        return instance;
    }

    private Persistence() {}

    public void execute() {
        if (timer == null) {
            start();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop();
                    start();
                }
            }, ONE_HOUR, ONE_HOUR);
        }
    }

    public void shutdown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            stop();
        }
    }

    private synchronized void start() {
        if (thread != null && (!thread.isInterrupted())) {
            EntryPoint.LOGGER.println("persistence thread already started");
            return;
        }
        EntryPoint.LOGGER.println("persistence thread starting");
        Date date = new Date();
        String dateStr = DATE_FORMAT.format(date);
        String timeStr = TIME_FORMAT.format(date);
        String userHome = System.getProperty("user.home");
        String path = userHome + File.separator + "interaction_traces" + File.separator + "intellij" + File.separator +
                dateStr + File.separator + timeStr + "_" + date.getTime() + ".json";
        writer.open(path);

        thread = new MyThread();
        thread.start();
        try {
            threadStartSema.acquire();
        } catch (InterruptedException e) {
            // wait thread started
        }
        isClosed = false;
        EntryPoint.LOGGER.println("persistence thread started");
    }

    private synchronized void stop() {
        if (thread == null || thread.isInterrupted()) {
            EntryPoint.LOGGER.println("persistence thread already stopped");
            return;
        }
        EntryPoint.LOGGER.println("persistence thread stopping");
        isClosed = true;
        thread.interrupt();
        try {
            threadStopSema.acquire();
        } catch (InterruptedException e) {
            // wait cached data is saved to disk
        }
        thread = null;
        EntryPoint.LOGGER.println("persistence thread stopped");
    }

    public synchronized void add(IntelliJEvent event) {
        if (isClosed) {
            return;
        }

        if (event == null) {
            return;
        }

        queue.addLast(event);
        queueSema.release();
    }

    private synchronized IntelliJEvent remove() {
        if (queue.isEmpty()) {
            return null;
        } else {
            return queue.removeFirst();
        }
    }
}
