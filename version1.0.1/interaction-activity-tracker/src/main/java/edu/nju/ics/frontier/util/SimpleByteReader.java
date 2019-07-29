package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * Created by zzw on 2018/4/3.
 */
public class SimpleByteReader {
    private DataInputStream inputStream = null;

    public DataInputStream inputStream() {
        return inputStream;
    }

    public void open(InputStream is) throws IOException {
        if (is == null) {
            return;
        }

        close();

        if (is instanceof DataInputStream) {
            inputStream = (DataInputStream) is;
        } else if (is instanceof BufferedInputStream) {
            inputStream = new DataInputStream(is);
        } else {
            inputStream = new DataInputStream(new BufferedInputStream(is));
        }
    }

    public void open(String filename) throws IOException {
        if ((filename == null) || (filename.length() <= 0)) {
            return;
        }

        open(new File(filename));
    }

    public void open(File file) throws IOException {
        if ((file == null) || (!file.exists()) || (!file.isFile())) {
            return;
        }

        close();

        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    public int read(byte[] buffer) throws IOException {
        if (inputStream == null) {
            return -1;
        }

        return inputStream.read(buffer);
    }

    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }
}
