package edu.nju.ics.frontier.listener;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * <strong>MyActionListener</strong> implements <em>AnActionListener</em> to listen the
 * actions triggered by a left mouse click, or a keyboard shortcut, or a combination of
 * keystroke and mouse-click (e.g., 'ctrl-click navigation').
 * An action is managed by the <a href="http://www.jetbrains.org/intellij/sdk/docs/tutorials/action_system.html">IntelliJ Action System</a>
 * and can be registered in the file <em>plugin.xml</em>.
 */
public class MyActionListener implements AnActionListener {
    /**
     * the category of activities listened by this listener
     */
    private static final String TYPE = "Action";

    /**
     * mapping between the action class to the action id
     */
    private Map<String, String> class2id;

    /**
     * Constructor
     */
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

    /**
     * construct an <strong>IntelliJEvent</strong> for an action,
     * and persist it in local file system.
     * the specific data for the action IntelliJEvent consists:
     * <ul>
     *     <li>id: id of this action (e.g., ChooseRunConfiguration)</li>
     *     <li>class: class that implements this action (e.g., com.intellij.execution.actions.ChooseRunConfigurationPopupAction)</li>
     *     <li>action: description of this action (e.g., Run... (Choose and run configuration))</li>
     * </ul>
     * @param time occurrence time of this action
     * @param when when action performed
     * @param anAction the action
     */
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
