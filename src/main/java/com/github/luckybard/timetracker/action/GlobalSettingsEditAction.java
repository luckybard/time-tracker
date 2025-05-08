package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.PropertiesController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GlobalSettingsEditAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PropertiesController service = Objects.requireNonNull(anActionEvent.getProject()).getService(PropertiesController.class);
        service.changeSettings();
    }
}
