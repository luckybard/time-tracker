package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClearSessionHistoryAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        SessionService service = Objects.requireNonNull(anActionEvent.getProject()).getService(SessionService.class);
        service.clearSessions();
    }
}
