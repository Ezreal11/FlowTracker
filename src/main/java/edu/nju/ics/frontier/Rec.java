package edu.nju.ics.frontier;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class Rec extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        int result=-1;
        Recognition_module rm=Recognition_module.getInstance();
        try {
            String userHome = System.getProperty("user.home");
            String rootpath = userHome  + "/interaction_traces/";
            //leave:jython不支持计算worktime的total_seconds

            result=rm.getResult("1");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Messages.showMessageDialog("Recognition Result Is:  "+Integer.toString(result), "test", Messages.getInformationIcon());

    }
}
