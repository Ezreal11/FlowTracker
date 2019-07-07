package com.zzw.listener.topic;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.zzw.compat.AppInfo;
//Topic事件管理器
public class MyTopicAgent implements Disposable {
    private AnActionListener actionListener;

    public MyTopicAgent() {
        this.actionListener = new MyActionListener();

        ActionManager.getInstance().addAnActionListener(actionListener, this);

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
