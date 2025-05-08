package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.TrackerController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StartTrackingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        TrackerController service = Objects.requireNonNull(anActionEvent.getProject()).getService(TrackerController.class);
        service.startTracking();
    }
}
