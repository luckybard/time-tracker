package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.PropertiesController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class GlobalSettingsEditAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PropertiesController service = requireNonNull(anActionEvent.getProject()).getService(PropertiesController.class);
        service.changeSettings();
    }
}
