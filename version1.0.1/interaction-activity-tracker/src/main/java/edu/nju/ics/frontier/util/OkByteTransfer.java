package edu.nju.ics.frontier.util;

import java.io.File;

/**
 * Created by zzw on 2018/4/3.
 */
public class OkByteTransfer {
    private OkByteReader reader = new OkByteReader();
    private OkByteWriter writer = new OkByteWriter();

    public int[] transfer(String from, String to) {
        return transfer(from, to, 10 * 1024 * 1024);
    }

    public int[] transfer(String from, String to, int bufferSize) {
        reader.open(from);
        writer.open(to);

        int fromBytes = 0;
        int toBytes = 0;

        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = reader.read(buffer)) > 0) {
            fromBytes += len;
            Buffer modifiedBuffer = modifyBuffer(buffer, 0, len);
            toBytes += modifiedBuffer.getLength();

            writer.write(modifiedBuffer.getBuffer(), modifiedBuffer.getOffset(), modifiedBuffer.getLength());
        }

        reader.close();
        writer.close();

        return new int[]{ fromBytes, toBytes };
    }

    public int[] transfer(File fromFile, File toFile) {
        return transfer(fromFile.getAbsolutePath(), toFile.getAbsolutePath());
    }

    public int[] transfer(File fromFile, File toFile, int bufferSize) {
        return transfer(fromFile.getAbsolutePath(), toFile.getAbsolutePath(), bufferSize);
    }

    public Buffer modifyBuffer(byte[] buffer, int offset, int length) {
        return new Buffer(buffer, offset, length);
    }

    public static class Buffer {
        private byte[] buffer;
        private int offset;
        private int length;

        public Buffer(byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public void setBuffer(byte[] buffer) {
            this.buffer = buffer;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }
}
