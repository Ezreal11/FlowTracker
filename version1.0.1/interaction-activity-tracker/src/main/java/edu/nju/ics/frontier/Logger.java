package edu.nju.ics.frontier;

import edu.nju.ics.frontier.util.IntelliJInfo;
import edu.nju.ics.frontier.util.OkTextWriter;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private OkTextWriter writer = new OkTextWriter();
    private static Logger instance;

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    private Logger() {
        Date date = new Date();
        String userHome = System.getProperty("user.home");
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
        String timeStr = new SimpleDateFormat("HHmmss").format(date);
        String path = userHome + File.separator + "interaction_traces" + File.separator + "plugin_log" + File.separator +
                dateStr + File.separator + timeStr + "_" + date.getTime() + ".log";
        writer.open(path);
        writer.println(IntelliJInfo.getAllVersion());
    }

    public void println(Object obj) {
        writer.println(obj);
    }

    public void printf(String format, Object... args) {
        writer.printf(format, args);
    }

    public void print(Object obj) {
        writer.print(obj);
    }

    public void println() {
        writer.println();
    }

    public PrintWriter writer() {
        return writer.writer();
    }
}
