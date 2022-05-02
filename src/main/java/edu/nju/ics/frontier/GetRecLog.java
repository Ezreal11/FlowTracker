package edu.nju.ics.frontier;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;

public class GetRecLog extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        try {
            Recognition_module.getInstance().getLog();
            String userHome = System.getProperty("user.home");
            String rootpath = userHome  + "\\interaction_traces\\";
            Messages.showMessageDialog("Recognition Log Is In folder:  "+rootpath, "Logging successgully!", Messages.getInformationIcon());
        } catch (IOException e1) {
            e1.printStackTrace();
            Messages.showMessageDialog("Recognition Logging failed", "Logging failed!", Messages.getInformationIcon());
        }
    }
}
