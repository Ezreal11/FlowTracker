package edu.nju.ics.frontier.util;

import java.io.File;

/**
 * Created by zzw on 2018/4/3.
 */
public class OkTextTransfer {
    private OkTextReader reader = new OkTextReader();
    private OkTextWriter writer = new OkTextWriter();

    public int[] transfer(String from, String to) {
        reader.open(from);
        writer.open(to);

        int fromLength = 0;
        int toLength = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            fromLength += line.length();
            String modifiedLine = modifyLine(line);
            toLength += modifiedLine.length();

            writer.println(modifiedLine);
        }

        reader.close();
        writer.close();

        return new int[]{ fromLength, toLength };
    }

    public int[] transfer(File fromFile, File toFile) {
        return transfer(fromFile.getAbsolutePath(), toFile.getAbsolutePath());
    }

    public String modifyLine(String line) {
        return line;
    }
}
