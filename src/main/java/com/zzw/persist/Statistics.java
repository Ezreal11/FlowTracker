package com.zzw.persist;

import com.zzw.EntryPoint;
import com.zzw.scale.model.AnswerModel;
import com.zzw.scale.view.LikertScaleDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

//数据可视化模块，展现前一天交互数据统计情况
public class Statistics implements LikertScaleDialog.ConfirmListener {
    public static final int KEY_RELEASED = 0;
    public static final int MOUSE_CLICKED = 1;
    public static final int WINDOW_ACTIVATED = 2;
    public static final int WINDOW_DEACTIVATED = 3;
    public static final int SCALE_SUBMITTED = 4;

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private LinkedList<TimeEvent> queue = new LinkedList<>();
    private Semaphore queueSema = new Semaphore(0);
    private Semaphore threadStartSema = new Semaphore(0);
    private Semaphore threadStopSema = new Semaphore(0);
    private boolean isClosed = true;
    private Thread thread;
    private Timer timer;

    private static Statistics instance;

    class MyThread extends Thread {
        private long timeBase;
        private long timeStep;
        private Map<Integer, long[]> keyMouseCounter;
        private List<Long> windowCounter;
        private List<Performance> scaleCounter;
        private boolean isWindowActivated;

        public MyThread() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.timeBase = calendar.getTimeInMillis();
            this.timeStep = ONE_HOUR;
            this.keyMouseCounter = new HashMap<>();
            this.windowCounter = new ArrayList<>();
            this.scaleCounter = new ArrayList<>();
            this.isWindowActivated = false;
        }

        @Override
        public void run() {
            try {
                threadStartSema.release();
                while (!isInterrupted()) {
                    queueSema.acquire();
                    TimeEvent te = remove();
                    count(te);
                }
            } catch (InterruptedException e) {
                // shutdown this thread
            } finally {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    TimeEvent te = queue.get(i);
                    count(te);
                }
                if (isWindowActivated) {
                    windowCounter.add(System.currentTimeMillis());
                    isWindowActivated = false;
                }
                queue.clear();

                String path = System.getProperty("user.home") + File.separator +
                        "interaction_traces" + File.separator + "statistics" + File.separator +
                        "data" + File.separator + DATE_FORMAT.format(new Date()) + File.separator;
                long time = System.currentTimeMillis();

                List<Input> inputData = count2input();
                if (!(inputData == null || inputData.isEmpty())) {
                    JsonIO.writeInputData(path + time + "_input.json", inputData);
                }

                List<Development> developmentData = count2development();
                if (!(developmentData == null || developmentData.isEmpty())) {
                    JsonIO.writeDevelopmentData(path + time + "_development.json", developmentData);
                }

                List<Performance> performanceData = count2performance();
                if (!(performanceData == null || performanceData.isEmpty())) {
                    JsonIO.writePerformanceData(path + time + "_performance.json", performanceData);
                }

                threadStopSema.release();
            }
        }

        private void count(TimeEvent te) {
            if (te == null) {
                return;
            }

            switch (te.event) {
                case KEY_RELEASED: {
                    int key = (int) ((te.time - timeBase) / timeStep);
                    long[] value = keyMouseCounter.get(key);
                    if (value == null) {
                        value = new long[]{0, 0};
                    }
                    value[0]++;
                    keyMouseCounter.put(key, value);
                    break;
                }
                case MOUSE_CLICKED: {
                    int key = (int) ((te.time - timeBase) / timeStep);
                    long[] value = keyMouseCounter.get(key);
                    if (value == null) {
                        value = new long[]{ 0, 0 };
                    }
                    value[1]++;
                    keyMouseCounter.put(key, value);
                    break;
                }
                case WINDOW_ACTIVATED: {
                    if (!isWindowActivated) {
                        windowCounter.add(te.time);
                        isWindowActivated = true;
                    }
                    break;
                }
                case WINDOW_DEACTIVATED: {
                    if (isWindowActivated) {
                        windowCounter.add(te.time);
                        isWindowActivated = false;
                    }
                    break;
                }
                case SCALE_SUBMITTED: {
                    long time = te.time;
                    int engagement = te.getExtraData("engagement");
                    int efficiency = te.getExtraData("efficiency");
                    Performance performance = new Performance(time, engagement, efficiency);
                    scaleCounter.add(performance);
                    break;
                }
                default:
                    break;
            }
        }

        private List<Input> count2input() {
            if (keyMouseCounter == null || keyMouseCounter.isEmpty()) {
                return null;
            }
            List<Input> result = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                long[] value = keyMouseCounter.get(i);
                if (value == null) {
                    value = new long[]{0, 0};
                }
                long begTime = timeBase + i * timeStep;
                long endTime = begTime + timeStep;
                int keystrokeNum = (int) value[0];
                int mouseClickNum = (int) value[1];
                Input input = new Input(begTime, endTime, keystrokeNum, mouseClickNum);
                result.add(input);
            }
            return result;
        }

        private List<Development> count2development() {
            if (windowCounter == null || windowCounter.isEmpty()) {
                return null;
            }
            List<Development> result = new ArrayList<>();
            for (int i = 0; i < windowCounter.size() - 1; i += 2) {
                long activatedTime = windowCounter.get(i);
                long deactivatedTime = windowCounter.get(i + 1);
                Development development = new Development(activatedTime, deactivatedTime);
                result.add(development);
            }
            return result;
        }

        private List<Performance> count2performance() {
            if (scaleCounter == null || scaleCounter.isEmpty()) {
                return null;
            }
            return new ArrayList<>(scaleCounter);
        }
    }

    public static Statistics getInstance() {
        if (instance == null) {
            synchronized (Statistics.class) {
                if (instance == null) {
                    instance = new Statistics();
                }
            }
        }
        return instance;
    }

    private Statistics() {}

    public void execute() {
        if (timer == null) {
            start();
            long delay = ONE_HOUR - (System.currentTimeMillis() % ONE_HOUR);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop();
                    start();
                }
            }, delay, ONE_HOUR);
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
            EntryPoint.LOGGER.println("statistics thread already started");
            return;
        }
        EntryPoint.LOGGER.println("statistics thread starting");
        thread = new MyThread();
        thread.start();
        try {
            threadStartSema.acquire();
        } catch (InterruptedException e) {
            // wait thread started
        }
        isClosed = false;
        EntryPoint.LOGGER.println("statistics thread started");
    }

    private synchronized void stop() {
        if (thread == null || thread.isInterrupted()) {
            EntryPoint.LOGGER.println("statistics thread already stopped");
            return;
        }
        EntryPoint.LOGGER.println("statistics thread stopping");
        isClosed = true;
        thread.interrupt();
        try {
            threadStopSema.acquire();
        } catch (InterruptedException e) {
            // wait cached data is saved to disk
        }
        thread = null;
        EntryPoint.LOGGER.println("statistics thread stopped");
    }

    public synchronized void add(long time, int event) {
        if (isClosed) {
            return;
        }
        queue.addLast(new TimeEvent(time, event));
        queueSema.release();
    }

    public synchronized void add(TimeEvent te) {
        if (isClosed) {
            return;
        }
        queue.addLast(te);
        queueSema.release();
    }

    private synchronized TimeEvent remove() {
        if (queue.isEmpty()) {
            return null;
        } else {
            return queue.removeFirst();
        }
    }

    @Override
    public void scaleOpened() {}

    @Override
    public void confirmed(List<AnswerModel> list) {
        TimeEvent te = new TimeEvent(System.currentTimeMillis(), SCALE_SUBMITTED);
        if (!(list == null || list.isEmpty())) {
            for (AnswerModel model : list) {
                String key = model.getTitle();
                int value = model.getOptionScore();
                te.addExtraData(key, value);
            }
        }
        add(te);
    }

    @Override
    public void scaleClosing() {}

    class TimeEvent {
        final long time;
        final int event;
        final Map<String, Integer> extraData = new HashMap<>();

        public TimeEvent(long time, int event) {
            this.time = time;
            this.event = event;
        }

        public void addExtraData(String key, int value) {
            extraData.put(key, value);
        }

        public int getExtraData(String key) {
            Integer value = extraData.get(key);
            return (value == null) ? -1 : value;
        }
    }
}
