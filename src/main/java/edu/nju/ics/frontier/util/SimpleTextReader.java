package edu.nju.ics.frontier.util;

import java.io.*;

/**
 * This class is a simple encapsulation of read operation for local files
 */
public class SimpleTextReader {
    /**
     * buffered reader
     */
    private BufferedReader bufferedReader = null;

    /**
     * get the buffered reader
     * @return the buffered reader
     */
    public BufferedReader reader() {
        return bufferedReader;
    }

    /**
     * open an input stream from an exist reader.
     * @param reader an opened reader
     * @throws IOException
     */
    public void open(Reader reader) throws IOException {
        if (reader == null) {
            return;
        }

        close();

        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }

    /**
     * open an input stream from a exist input stream.
     * @param is an opened input stream
     * @throws IOException
     */
    public void open(InputStream is) throws IOException {
        if (is == null) {
            return;
        }

        close();

        bufferedReader = new BufferedReader(new InputStreamReader(is));
    }

    /**
     * open an input stream from an absolute file path
     * @param filename the absolute path of a file
     * @throws IOException
     */
    public void open(String filename) throws IOException {
        if ((filename == null) || (filename.length() <= 0)) {
            return;
        }
        open(new File(filename));
    }

    /**
     * open an input stream from a file
     * @param file a file
     * @throws IOException
     */
    public void open(File file) throws IOException {
        if ((file == null) || (!file.exists()) || (!file.isFile())) {
            return;
        }

        close();

        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }

    /**
     * read one line from the opened input stream
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException {
        if (bufferedReader == null) {
            return null;
        }
        return bufferedReader.readLine();
    }

    /**
     * close the input stream
     * @throws IOException
     */
    public void close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
            bufferedReader = null;
        }
    }
}
