package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.PropertiesController;
import com.github.luckybard.timetracker.listener.ProjectCloseListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class GlobalSettingsEditAction extends AnAction {
    private static final Logger logger = LoggerFactory.getLogger(GlobalSettingsEditAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        logger.debug("GlobalSettingsEditAction::actionPerformed()");
        PropertiesController service = requireNonNull(anActionEvent.getProject()).getService(PropertiesController.class);
        service.changeSettings();
    }
}
