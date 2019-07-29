package edu.nju.ics.frontier.listener;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import edu.nju.ics.frontier.persist.IntelliJEvent;
import edu.nju.ics.frontier.persist.Persistence;
import edu.nju.ics.frontier.util.AWTUtil;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * <strong>MyAWTEventListener</strong> listens the activities from keyboard, mouse and
 * IDE window. Although these three listeners implement <em>AWTEventListener</em>, the
 * events listened by these listeners are dispatched from the <em>IdeEventQueue</em>,
 * and the life cycle of these listeners is managed by <em>Disposer</em>. It means that
 * MyAWTEventListener only listens the key, mouse and window activities occurred in IDE,
 * the activities out of IDE are not listened.
 *
 * the specific data for the key IntelliJEvent consists:
 * <ul>
 *     <li>text: fuzzy character (e.g., Letter)</li>
 *     <li>modifiers: modifiers of this key event (e.g., Ctrl)</li>
 * </ul>
 *
 * the specific data for the mouse IntelliJEvent consists:
 * <ul>
 *     <li>coordinates: absolute and relative coordinates of this mouse event (e.g., (416,109)/(41,5))</li>
 *     <li>button: button pressed of this mouse event (e.g., 1)</li>
 *     <li>modifiers: modifiers of this mouse event (e.g., Ctrl)</li>
 *     <li>clickCount: click count of this mouse event (e.g., 1)</li>
 * </ul>
 *
 * the specific data for the window IntelliJEvent consists:
 * <ul>
 *     <li>this: name and type of this window event (e.g., win1,POPUP)</li>
 *     <li>opposite: name and type of the opposite window of this window event (e.g., frame0,NORMAL)</li>
 * </ul>
 */
public class MyAWTEventListener extends MyListener implements Disposable {
    private AWTEventListener keystrokeListener;
    private AWTEventListener mouseListener;
    private AWTEventListener windowListener;
    private final List<ActivationListener> listeners = new ArrayList<>();

    public void addActivationListener(ActivationListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
        }
    }

    public void removeActivationListener(ActivationListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public interface ActivationListener {
        void keyActivated(long time, KeyEvent e);
        void mouseActivated(long time, MouseEvent e);
        void windowActivated(long time, WindowEvent e);
        void windowDeactivated(long time, WindowEvent e);
    }

    public MyAWTEventListener(Persistence persistence) {
        super(persistence, "AWT");

        keystrokeListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event == null || (!(event instanceof KeyEvent))) {
                    return;
                }
                KeyEvent e = (KeyEvent) event;
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(),
                        "Key", AWTUtil.getKeyId(e.getID()));
                ie.addExtraData("text", AWTUtil.getKeyText(e.getKeyCode()));
                ie.addExtraData("modifiers", AWTUtil.getKeyModifiers(e.getModifiers()));
                getPersistence().add(ie);

                if (!(listeners == null || listeners.isEmpty())) {
                    long time = System.currentTimeMillis();
                    for (ActivationListener l : listeners) {
                        l.keyActivated(time, e);
                    }
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(keystrokeListener,
                AWTEvent.KEY_EVENT_MASK);

        mouseListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!(event instanceof MouseEvent)) {
                    return;
                }
                MouseEvent e = (MouseEvent) event;
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(),
                        "Mouse", AWTUtil.getMouseId(e.getID()));
                ie.addExtraData("coordinates", AWTUtil.getMouseCoordinates(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY()));
                ie.addExtraData("button", AWTUtil.getMouseButton(e.getID(), e.getButton()));
                ie.addExtraData("modifiers", AWTUtil.getMouseModifiers(e.getModifiers()));
                ie.addExtraData("clickCount", AWTUtil.getMouseClickCount(e.getClickCount()));
                getPersistence().add(ie);
//                System.out.println(e.paramString());

                if (!(listeners == null || listeners.isEmpty())) {
                    long time = System.currentTimeMillis();
                    for (ActivationListener l : listeners) {
                        l.mouseActivated(time, e);
                    }
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(mouseListener,
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK |
                        AWTEvent.MOUSE_WHEEL_EVENT_MASK);

        windowListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!(event instanceof WindowEvent)) {
                    return;
                }
                WindowEvent e = (WindowEvent) event;
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(),
                        "Window", AWTUtil.getWindowId(e.getID()));
                ie.addExtraData("this", AWTUtil.getWindowInfo(e.getWindow()));
                ie.addExtraData("opposite", AWTUtil.getWindowInfo(e.getOppositeWindow()));
//                ie.addExtraData("oldState", AWTUtil.getWindowState(e.getOldState()));
//                ie.addExtraData("newState", AWTUtil.getWindowState(e.getNewState()));
                getPersistence().add(ie);
//                System.out.println("[" + projectName + "] " + e.paramString());

                long time = System.currentTimeMillis();
                switch (e.getID()) {
                    case WindowEvent.WINDOW_ACTIVATED: {
                        if (!(listeners == null || listeners.isEmpty())) {
                            for (ActivationListener l : listeners) {
                                l.windowActivated(time, e);
                            }
                        }
                        break;
                    }
                    case WindowEvent.WINDOW_DEACTIVATED: {
                        if (!(listeners == null || listeners.isEmpty())) {
                            for (ActivationListener l : listeners) {
                                l.windowDeactivated(time, e);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(windowListener,
                AWTEvent.WINDOW_EVENT_MASK |
                        AWTEvent.WINDOW_FOCUS_EVENT_MASK |
                        AWTEvent.WINDOW_STATE_EVENT_MASK);
    }

    @Override
    public void dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(keystrokeListener);
        Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener);
        Toolkit.getDefaultToolkit().removeAWTEventListener(windowListener);
    }
}
