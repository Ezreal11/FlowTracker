package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * This class is a further encapsulation of the <em>SimpleTextReader</em>
 * that handles the exceptions thrown by methods in <em>SimpleTextReader</em>.
 * We suggest that to use this class if you ensure the exceptions will not occurred,
 * otherwise the <em>SimpleTextReader</em> is suggested.
 */
public class OkTextReader {
    /**
     * simple text reader
     */
    private SimpleTextReader reader = new SimpleTextReader();

    /**
     * @see SimpleTextReader#reader()
     * @return
     */
    public BufferedReader reader() {
        return reader.reader();
    }

    /**
     * @see SimpleTextReader#open(Reader)
     * @param reader
     */
    public void open(Reader reader) {
        try {
            this.reader.open(reader);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextReader#open(InputStream)
     * @param is
     */
    public void open(InputStream is) {
        try {
            this.reader.open(is);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextReader#open(String)
     * @param filename
     */
    public void open(String filename) {
        try {
            reader.open(filename);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextReader#open(File)
     * @param file
     */
    public void open(File file) {
        try {
            reader.open(file);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    /**
     * @see SimpleTextReader#readLine()
     * @return
     */
    public String readLine() {
        String line = null;

        try {
            line = reader.readLine();
        } catch (IOException e) {
            caughtException(e);
        }

        return line;
    }

    /**
     * @see SimpleTextReader#close()
     */
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            caughtException(e);
        }
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
