package edu.nju.ics.frontier.agent;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import edu.nju.ics.frontier.listener.MyAWTEventListener;
import edu.nju.ics.frontier.view.ScaleManager;

/**
 * <strong>MyAWTEventAgent</strong> manages the key, mouse and window listeners,
 * which listens the key, mouse and window activities triggered in IDE and omits
 * the activities out of IDE.
 */
public class MyAWTEventAgent implements Disposable {
    private MyAWTEventListener awtEventListener;

    public MyAWTEventAgent() {
        awtEventListener = new MyAWTEventListener();
        awtEventListener.addActivationListener(ScaleManager.getInstance());
        // dispose the key, mouse and window listeners when 'MyAWTEventAgent' is disposed
        Disposer.register(this, awtEventListener);
    }

    @Override
    public void dispose() {}
}
