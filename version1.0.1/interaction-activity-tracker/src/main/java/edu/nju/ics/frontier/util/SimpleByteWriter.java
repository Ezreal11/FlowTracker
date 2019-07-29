package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * Created by zzw on 2018/4/3.
 */
public class SimpleByteWriter {
    private DataOutputStream outputStream = null;

    public DataOutputStream outputStream() {
        return outputStream;
    }

    public void open(OutputStream os) throws IOException {
        if (os == null) {
            return;
        }

        close();

        if (os instanceof DataOutputStream) {
            outputStream = (DataOutputStream) os;
        } else if (os instanceof BufferedOutputStream) {
            outputStream = new DataOutputStream(os);
        } else {
            outputStream = new DataOutputStream(new BufferedOutputStream(os));
        }
    }

    public void open(String filename) throws IOException {
        open(filename, true);
    }

    public void open(String filename, boolean isCoverExistingFile) throws IOException {
        if ((filename == null) || (filename.length() <= 0)) {
            return;
        }

        open(new File(filename), isCoverExistingFile);
    }

    public void open(File file) throws IOException {
        open(file, true);
    }

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

        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        if (outputStream == null) {
            return;
        }
        outputStream.write(buffer, offset, length);
        outputStream.flush();
    }

    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        }
    }
}
