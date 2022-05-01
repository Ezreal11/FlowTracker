package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * This class is a simple encapsulation of write operation for local files
 */
public class SimpleTextWriter {
    /**
     * print writer
     */
    private PrintWriter printWriter = null;

    /**
     * get the print writer
     * @return print writer
     */
    public PrintWriter writer() {
        return printWriter;
    }

    /**
     * open an output stream from a exist writer
     * @param writer an opened writer
     */
    public void open(Writer writer) {
        if (writer == null) {
            return;
        }

        close();

        if (writer instanceof PrintWriter) {
            printWriter = (PrintWriter) writer;
        } else {
            printWriter = new PrintWriter(writer);
        }
    }

    /**
     * open an output stream from an exist output stream
     * @param os an opened output stream
     */
    public void open(OutputStream os) {
        if (os == null) {
            return;
        }

        close();

        printWriter = new PrintWriter(os);
    }

    /**
     * @see SimpleTextWriter#open(File, boolean)
     * @param filename
     * @throws IOException
     */
    public void open(String filename) throws IOException {
        open(filename, true);
    }

    /**
     * @see SimpleTextWriter#open(File, boolean)
     * @param filename
     * @param isCoverExistingFile
     * @throws IOException
     */
    public void open(String filename, boolean isCoverExistingFile) throws IOException {
        if ((filename == null) || (filename.length() <= 0)) {
            return;
        }
        open(new File(filename), isCoverExistingFile);
    }

    /**
     * @see SimpleTextWriter#open(File, boolean)
     * @param file a file
     * @throws IOException
     */
    public void open(File file) throws IOException {
        open(file, true);
    }

    /**
     * open an output stream from a file:
     * <ul>
     *     <li>create the file if it doesn't exist;</li>
     *     <li>cover the file if it exists and the parameter <em>isCoverExistingFile</em> is <em>false</em>;</li>
     *     <li>throw <em>IOException</em> if the file exists and the parameter <em>isCoverExistingFile</em> is <em>true</em>.</li>
     * </ul>
     * @param file a file
     * @param isCoverExistingFile true to cover the content of the file, false to keep the content of the file.
     * @throws IOException thrown if the file exists and the parameter <em>isCoverExistingFile</em> is <em>true</em>.
     */
    public void open(File file, boolean isCoverExistingFile) throws IOException {
        if (file == null) {
            return;
        }

        if ((!isCoverExistingFile) && file.exists()) {
            throw new IOException(file.getAbsolutePath() + " already exists!");
        }

        close();

        File parentFile = file.getParentFile();
        if ((parentFile != null) && (!parentFile.exists())) {
            parentFile.mkdirs();
        }

        printWriter = new PrintWriter(new FileOutputStream(file));
    }

    /**
     * @see PrintWriter#printf(String, Object...)
     * @param format
     * @param args
     */
    public void printf(String format, Object... args) {
        if (printWriter == null) {
            return;
        }
        printWriter.printf(format, args);
        printWriter.flush();
    }

    /**
     * @see PrintWriter#print(Object)
     * @param obj
     */
    public void print(Object obj) {
        if (printWriter == null) {
            return;
        }
        printWriter.print(obj);
        printWriter.flush();
    }

    /**
     * @see PrintWriter#println(Object)
     * @param obj
     */
    public void println(Object obj) {
        if (printWriter == null) {
            return;
        }
        printWriter.println(obj);
        printWriter.flush();
    }

    /**
     * @see PrintWriter#println()
     */
    public void println() {
        if (printWriter == null) {
            return;
        }
        printWriter.println();
        printWriter.flush();
    }

    /**
     * close the output stream
     */
    public void close() {
        if (printWriter != null) {
            printWriter.flush();
            printWriter.close();
            printWriter = null;
        }
    }
}
