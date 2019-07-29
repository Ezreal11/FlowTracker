package edu.nju.ics.frontier.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zzw on 2018/4/3.
 */
public class OkByteReader {
    private SimpleByteReader reader = new SimpleByteReader();

    public DataInputStream inputStream() {
        return reader.inputStream();
    }

    public void open(InputStream is) {
        try {
            reader.open(is);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(String filename) {
        try {
            reader.open(filename);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(File file) {
        try {
            reader.open(file);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public int read(byte[] buffer) {
        int len = -1;

        try {
            len = reader.read(buffer);
        } catch (IOException e) {
            caughtException(e);
        }

        return len;
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void caughtException(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }
}
