package edu.nju.ics.frontier.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zzw on 2018/4/3.
 */
public class OkByteWriter {
    private SimpleByteWriter writer = new SimpleByteWriter();

    public DataOutputStream outputStream() {
        return writer.outputStream();
    }

    public void open(OutputStream os) {
        try {
            writer.open(os);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(String filename) {
        try {
            writer.open(filename);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(String filename, boolean isCoverExistingFile) {
        try {
            writer.open(filename, isCoverExistingFile);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(File file) {
        try {
            writer.open(file);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void open(File file, boolean isCoverExistingFile) {
        try {
            writer.open(file, isCoverExistingFile);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void write(byte[] buffer, int offset, int length) {
        try {
            writer.write(buffer, offset, length);
        } catch (IOException e) {
            caughtException(e);
        }
    }

    public void close() {
        try {
            writer.close();
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
