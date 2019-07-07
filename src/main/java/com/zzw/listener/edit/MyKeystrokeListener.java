package com.zzw.listener.edit;

import com.intellij.openapi.Disposable;
import com.zzw.persist.IntelliJEvent;
import com.zzw.persist.Persistence;
import com.zzw.persist.Statistics;
import com.zzw.util.AWTUtil;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
//键盘(Keyboard)事件监听器，记录每次键盘按键的内容
public class MyKeystrokeListener implements Disposable {
    private AWTEventListener keystrokeListener;
    private AWTEventListener mouseListener;
    private AWTEventListener windowListener;
    private final List<KeyActivationListener> listeners = new ArrayList<>();

    public void addKeyActivationListener(KeyActivationListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
        }
    }

    public void removeKeyActivationListener(KeyActivationListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public interface KeyActivationListener {
        void keyActivated(long time, KeyEvent e);
        void mouseActivated(long time, MouseEvent e);
        void windowActivated(long time, WindowEvent e);
        void windowDeactivated(long time, WindowEvent e);
    }

    public MyKeystrokeListener() {
        keystrokeListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event == null || (!(event instanceof KeyEvent))) {
                    return;
                }
                KeyEvent e = (KeyEvent) event;
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(), "Key", AWTUtil.getKeyId(e.getID()));
                ie.addExtraData("text", AWTUtil.getKeyText(e.getKeyCode()));
                ie.addExtraData("modifiers", AWTUtil.getKeyModifiers(e.getModifiers()));
                Persistence.getInstance().add(ie);
//                System.out.println(e.paramString());

                if (!(listeners == null || listeners.isEmpty())) {
                    long time = System.currentTimeMillis();
                    for (KeyActivationListener l : listeners) {
                        l.keyActivated(time, e);
                    }
                }

                if (e.getID() == KeyEvent.KEY_RELEASED) {
                    Statistics.getInstance().add(System.currentTimeMillis(), Statistics.KEY_RELEASED);
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
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(), "Mouse", AWTUtil.getMouseId(e.getID()));
                ie.addExtraData("coordinates", AWTUtil.getMouseCoordinates(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY()));
                ie.addExtraData("button", AWTUtil.getMouseButton(e.getID(), e.getButton()));
                ie.addExtraData("modifiers", AWTUtil.getMouseModifiers(e.getModifiers()));
                ie.addExtraData("clickCount", AWTUtil.getMouseClickCount(e.getClickCount()));
                Persistence.getInstance().add(ie);
//                System.out.println(e.paramString());

                if (!(listeners == null || listeners.isEmpty())) {
                    long time = System.currentTimeMillis();
                    for (KeyActivationListener l : listeners) {
                        l.mouseActivated(time, e);
                    }
                }

                if (e.getID() == MouseEvent.MOUSE_CLICKED) {
                    Statistics.getInstance().add(System.currentTimeMillis(), Statistics.MOUSE_CLICKED);
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
                IntelliJEvent ie = new IntelliJEvent(System.currentTimeMillis(), "Window", AWTUtil.getWindowId(e.getID()));
                ie.addExtraData("this", AWTUtil.getWindowInfo(e.getWindow()));
                ie.addExtraData("opposite", AWTUtil.getWindowInfo(e.getOppositeWindow()));
//                ie.addExtraData("oldState", AWTUtil.getWindowState(e.getOldState()));
//                ie.addExtraData("newState", AWTUtil.getWindowState(e.getNewState()));
                Persistence.getInstance().add(ie);
//                System.out.println(e.paramString());

                long time = System.currentTimeMillis();
                switch (e.getID()) {
                    case WindowEvent.WINDOW_ACTIVATED: {
                        if (!(listeners == null || listeners.isEmpty())) {
                            for (KeyActivationListener l : listeners) {
                                l.windowActivated(time, e);
                            }
                        }
                        Statistics.getInstance().add(time, Statistics.WINDOW_ACTIVATED);
                        break;
                    }
                    case WindowEvent.WINDOW_DEACTIVATED: {
                        if (!(listeners == null || listeners.isEmpty())) {
                            for (KeyActivationListener l : listeners) {
                                l.windowDeactivated(time, e);
                            }
                        }
                        Statistics.getInstance().add(time, Statistics.WINDOW_DEACTIVATED);
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
