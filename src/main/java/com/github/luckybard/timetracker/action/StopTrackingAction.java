package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.service.TrackerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StopTrackingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        TrackerService service = Objects.requireNonNull(anActionEvent.getProject()).getService(TrackerService.class);
        service.stopTimer();
    }
}
