package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.ExcelController;
import com.github.luckybard.timetracker.listener.ProjectCloseListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ExportExcelAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(ExportExcelAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        logger.debug("ExportExcelAction::actionPerformed()");
        ExcelController service = Objects.requireNonNull(anActionEvent.getProject()).getService(ExcelController.class);
        service.promptUserAndExport();
    }
}
