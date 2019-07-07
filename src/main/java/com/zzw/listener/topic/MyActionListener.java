package com.zzw.listener.topic;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;

import java.util.HashMap;
import java.util.Map;

//Topic模块监视器，记录在IDEA里触发的功能事件(事件的开始)，例如“开始编译”、“开始执行”等
public class MyActionListener implements AnActionListener {
    private static final String TYPE = "Action";
    private Map<String, String> class2id;

    public MyActionListener() {
        ActionManager am = ActionManager.getInstance();
        String[] actionIds = am.getActionIds("");
        if (actionIds == null || actionIds.length <= 0) {
            return;
        }
        Map<String, String> class2id = new HashMap<>();
        for (String actionId : actionIds) {
            AnAction action = am.getAction(actionId);
            if (action != null) {
                String className = action.getClass().getName();
                class2id.put(className, actionId);
            }
        }
        this.class2id = class2id;
    }

    @Override
    public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {}

    @Override
    public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
        onActionPerformed(System.currentTimeMillis(), "performed", anAction);
    }

    @Override
    public void beforeEditorTyping(char c, DataContext dataContext) {}

    private void onActionPerformed(long time, String when, AnAction anAction) {
        String className = "";
        String actionId = "";
        String action = "";
        if (anAction != null) {
            className = anAction.getClass().getName();
            actionId = class2id.get(className);
            if (actionId == null) {
                actionId = "";
            }
            action = anAction.toString();
        }
        IntelliJEvent e = new IntelliJEvent(time, TYPE, when);
        e.addExtraData("id", actionId);
        e.addExtraData("class", className);
        e.addExtraData("action", action);
        Persistence.getInstance().add(e);
    }
}
