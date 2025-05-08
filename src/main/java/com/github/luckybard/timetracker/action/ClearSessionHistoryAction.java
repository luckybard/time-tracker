package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.SessionController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClearSessionHistoryAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        SessionController service = Objects.requireNonNull(anActionEvent.getProject()).getService(SessionController.class);
        service.clearSessions();
    }
}
