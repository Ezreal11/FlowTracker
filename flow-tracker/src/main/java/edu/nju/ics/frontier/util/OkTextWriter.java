package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * This class is a further encapsulation of the <em>SimpleTextWriter</em>
 * that handles the exceptions thrown by methods in <em>SimpleTextWriter</em>.
 * We suggest that to use this class if you ensure the exceptions will not occurred,
 * otherwise the <em>SimpleTextWriter</em> is suggested.
 */
public class OkTextWriter {
    /**
     * simple text writer
     */
    private SimpleTextWriter writer = new SimpleTextWriter();

    /**
     * @see SimpleTextWriter#writer()
     * @return
     */
    public PrintWriter writer() {
        return writer.writer();
    }

    /**
     * @see SimpleTextWriter#open(Writer)
     * @param writer
     */
    public void open(Writer writer) {
        this.writer.open(writer);
    }

    /**
     * @see SimpleTextWriter#open(OutputStream)
     * @param os
     */
    public void open(OutputStream os) {
        this.writer.open(os);
    }

    /**
     * @see SimpleTextWriter#open(String)
     * @param filename
     */
    public void open(String filename) {
        try {
            writer.open(filename);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextWriter#open(String, boolean)
     * @param filename
     * @param isCoverExistingFile
     */
    public void open(String filename, boolean isCoverExistingFile) {
        try {
            writer.open(filename, isCoverExistingFile);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextWriter#open(File)
     * @param file
     */
    public void open(File file) {
        try {
            writer.open(file);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextWriter#open(File, boolean)
     * @param file
     * @param isCoverExistingFile
     */
    public void open(File file, boolean isCoverExistingFile) {
        try {
            writer.open(file, isCoverExistingFile);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextWriter#printf(String, Object...)
     * @param format
     * @param args
     */
    public void printf(String format, Object... args) {
        writer.printf(format, args);
    }

    /**
     * @see SimpleTextWriter#print(Object)
     * @param obj
     */
    public void print(Object obj) {
        writer.print(obj);
    }

    /**
     * @see SimpleTextWriter#println(Object)
     * @param obj
     */
    public void println(Object obj) {
        writer.println(obj);
    }

    /**
     * @see SimpleTextWriter#println()
     */
    public void println() {
        writer.println();
    }

    /**
     * @see SimpleTextWriter#close()
     */
    public void close() {
        writer.close();
    }

    /**
     * you can handle the thrown exception by extending
     * this class and overriding this method.
     * @param e the thrown exception
     */
    public void caughtException(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }
}
