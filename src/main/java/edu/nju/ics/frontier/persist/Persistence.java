package edu.nju.ics.frontier.persist;

import com.google.gson.Gson;
import edu.nju.ics.frontier.EntryPoint;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

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
     * this SimpleDateFormat is used to format the name of the directory that places the IntelliJ event file
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * this SimpleDateFormat is used to format the name of the IntelliJ event file
     */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");

    /**
     * IntelliJ event queue
     */
    private LinkedList<IntelliJEvent> queue = new LinkedList<>();

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
     * writer that writes IntelliJ events into local file system
     */
    private OkTextWriter writer = new OkTextWriter();

    /**
     * Gson instance is used to convert an IntelliJ event instance to a text string
     */
    private Gson gson = new Gson();

    /**
     * indicate whether the IntelliJ event queue is open for IntelliJ events
     */
    private boolean isClosed = true;

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
     * the single instance of the Persistence class
     */
    private static Persistence instance;

    /**
     * persistence thread
     */
    class MyThread extends Thread {
        @Override
        public void run() {
            try {
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

    /**
     * get the single instance of the Persistence class
     * @return the single instance of the Persistence class
     */
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

    /**
     * the persistence thread
     */
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

    /**
     * add a captured IntelliJ event into the IntelliJ event queue
     * @param event the captured IntelliJ event
     */
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

    /**
     * remove the first captured IntelliJ event in the IntelliJ event queue
     * @return the first captured IntelliJ event in the IntelliJ event queue,
     * or null if the IntelliJ event queue is empty
     */
    private synchronized IntelliJEvent remove() {
        if (queue.isEmpty()) {
            return null;
        } else {
            return queue.removeFirst();
        }
    }
}
