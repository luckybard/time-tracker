package com.github.luckybard.timetracker.action;

import com.github.luckybard.timetracker.service.ExcelService;
import com.github.luckybard.timetracker.service.PropertiesService;
import com.github.luckybard.timetracker.service.TrackerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExportExcelAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ExcelService service = Objects.requireNonNull(anActionEvent.getProject()).getService(ExcelService.class);
        service.promptUserAndExport();
    }
}
