package edu.nju.ics.frontier.scale;

import edu.nju.ics.frontier.scale.control.LikertScale;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class EntryPoint {
    public static void main(String[] args) {
        boolean isTest = false;
        boolean block = false;
        String rootPath = null;
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.equals("-t")) {
                    isTest = true;
                } else if (arg.equals("-b")) {
                    block = true;
                } else {
                    rootPath = arg;
                }
            }
        }

        if (rootPath == null || rootPath.trim().length() <= 0) {
            rootPath = System.getProperty("user.home");
        }
        rootPath = rootPath + File.separator + "interaction_traces" + File.separator + "scale";

        LikertScale scale = null;

        if (isTest) {
            scale = new LikertScale(rootPath, block);
            scale.showLikertScaleDialog(true);
        } else {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (scale != null) {
                    scale.closeLikertScaleDialog();
                }
                scale = new LikertScale(rootPath, block);
                scale.showLikertScaleDialog(false);
            }
        }
    }
}
