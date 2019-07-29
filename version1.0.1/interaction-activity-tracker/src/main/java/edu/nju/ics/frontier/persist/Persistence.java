package edu.nju.ics.frontier.persist;

import com.google.gson.Gson;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class persists the captured IntelliJ events into local file system.
 * The persistence operation is done in a thread independent of the main thread,
 * which means persistence operations cannot block the main thread.
 */
public class Persistence {
    /**
     * latency to update the persistence output stream
     */
    private static final long ONE_HOUR = 60 * 60 * 1000;

    /**
     * IntelliJ event queue
     */
    private ConcurrentLinkedQueue<IntelliJEvent> queue = new ConcurrentLinkedQueue<>();

    /**
     * semaphore indicated whether an new IntelliJ event comes
     */
    private Semaphore queueSema = new Semaphore(0);

    /**
     * semaphore indicated the persistence thread started
     */
    private Semaphore threadStartSema = new Semaphore(0);

    /**
     * semaphore indicated the persistence thread stopped
     */
    private Semaphore threadStopSema = new Semaphore(0);

    /**
     * indicate whether the IntelliJ event queue is open for IntelliJ events
     */
    private AtomicBoolean isClosed = new AtomicBoolean(true);

    /**
     * persistence thread
     */
    private Thread thread;

    /**
     * timer to schedule the output stream of the captured IntelliJ events,
     * the output stream is updated per hour by default
     */
    private Timer timer;

    /**
     * persistence thread
     */
    class MyThread extends Thread {
        private OkTextWriter writer = new OkTextWriter();
        private Gson gson = new Gson();
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

        @Override
        public void run() {
            try {
                Date date = new Date();
                String dateStr = dateFormat.format(date);
                String timeStr = timeFormat.format(date);
                String userHome = System.getProperty("user.home");
                String path = userHome + File.separator + "interaction_traces" + File.separator + "intellij" + File.separator +
                        dateStr + File.separator + timeStr + "_" + date.getTime() + ".json";
                writer.open(path);

                isClosed.set(false);
                threadStartSema.release();

                while (!isInterrupted()) {
                    // acquire a captured IntelliJ event and persist it to local file system
                    queueSema.acquire();
                    IntelliJEvent event = remove();
                    if (event != null) {
                        writer.println(gson.toJson(event));
                    }
                }
            } catch (InterruptedException e) {
                // shutdown this thread
            } finally {
                // persist the IntelliJ events remained in IntelliJ event queue
                // after the persistence thread interrupted
                isClosed.set(true);
                for (IntelliJEvent event : queue) {
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

    private static Persistence instance;

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

    /**
     * start the timer and the persistence thread
     */
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

    /**
     * stop the timer and the persistence thread
     */
    public void shutdown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            stop();
        }
    }

    /**
     * start the persistence thread
     */
    private synchronized void start() {
        if (thread != null && thread.isAlive()) {
            return;
        }

        thread = new MyThread();
        thread.start();
        try {
            threadStartSema.acquire();
        } catch (InterruptedException e) {
            // wait thread started
        }
    }

    /**
     * the persistence thread
     */
    private synchronized void stop() {
        if (thread == null || thread.isInterrupted()) {
            return;
        }
        thread.interrupt();
        try {
            threadStopSema.acquire();
        } catch (InterruptedException e) {
            // wait cached data is saved to disk
        }
        thread = null;
    }

    /**
     * add a captured IntelliJ event into the IntelliJ event queue
     * @param event the captured IntelliJ event
     */
    public synchronized void add(IntelliJEvent event) {
        if (!(isClosed.get() || event == null)) {
            queue.add(event);
            queueSema.release();
        }
    }

    /**
     * remove the first captured IntelliJ event in the IntelliJ event queue
     * @return the first captured IntelliJ event in the IntelliJ event queue,
     * or null if the IntelliJ event queue is empty
     */
    private IntelliJEvent remove() {
        return queue.isEmpty() ? null : queue.poll();
    }
}
