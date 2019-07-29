package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.listener.MyActionListener;
import edu.nju.ics.frontier.persist.Persistence;

/**
 * <strong>MyActionAgent</strong> manages the action listener, which listens the actions triggered in IDE.
 */
public class MyActionAgent extends MyAgent {
    /**
     * the AnActionListener implemented as MyActionListener
     */
    private AnActionListener actionListener;

    /**
     * Constructor
     */
    public MyActionAgent(Persistence persistence) {
        super(persistence);

        this.actionListener = new MyActionListener(persistence);

        /* register the action listener through ActionManager */
        ActionManager.getInstance().addAnActionListener(actionListener, this);

        /*
         * suggest by the IntelliJ SDK, the IntelliJ IDEA whose version
         * is lower than '2018.3' doesn't has the 'AnActionListener.TOPIC',
         * the action listener can be only registered through 'ActionManager'.
         *
         * In version '2018.3', 'AnActionListener.TOPIC' is added and the
         * action listener can be registered through it.
         *
         * However, we try to install this plugin in other IDEs developed based
         * on the IntelliJ Platform such as Android Studio. We find that Android
         * Studio doesn't support 'AnActionListener.TOPIC'.
         *
         * Therefore we use 'ActionManager#addAnActionListener(AnActionListener, Disposable)'
         * even if it is deprecated.
         */

        /*
         * the following code snippet works in IntelliJ IDEA,
         * but loses efficacy in Android Studio!!!
         */
//        String platformVersion = AppInfo.getPlatformVersion();
//        if (platformVersion.compareTo("2018.3") < 0) {
//            ActionManager.getInstance().addAnActionListener(actionListener, this);
//        } else {
//            MessageBusConnection connection = ApplicationManager.getApplication().
//                    getMessageBus().connect();
//            Disposer.register(this, connection);
//            connection.subscribe(AnActionListener.TOPIC, actionListener);
//        }
    }

    @Override
    public void dispose() {
        ActionManager.getInstance().removeAnActionListener(actionListener);

//        String platformVersion = AppInfo.getPlatformVersion();
//        if (platformVersion.compareTo("2018.3") < 0) {
//            ActionManager.getInstance().removeAnActionListener(actionListener);
//        }
    }
}
