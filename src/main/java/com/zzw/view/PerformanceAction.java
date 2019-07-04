package com.zzw.view;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.zzw.scale.view.PerformanceFrame;
import org.jetbrains.annotations.NotNull;

public class PerformanceAction extends AnAction {
    private PerformanceFrame frame = new PerformanceFrame(0.5);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        frame.show();
    }
}
