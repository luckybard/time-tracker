package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.controller.ExcelController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExportExcelAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ExcelController service = Objects.requireNonNull(anActionEvent.getProject()).getService(ExcelController.class);
        service.promptUserAndExport();
    }
}
