package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.service.ExcelExporterService;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.ui.component.TimeTrackerComponents;
import com.github.luckybard.timetracker.ui.component.TimeTrackerController;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class TimeTrackerPanel {
    private final JPanel panel;
    private final TimeTrackerComponents components;
    private final TimeTrackerController controller;

    public TimeTrackerPanel(Project project) {
        ExcelExporterService excelExporterService = project.getService(ExcelExporterService.class);
        BranchTimeTrackerService sessionService = project.getService(BranchTimeTrackerService.class);
        SessionService trackerService = project.getService(SessionService.class);
        this.components = new TimeTrackerComponents(project);
        this.controller = new TimeTrackerController(excelExporterService, project, sessionService, trackerService, components);

        panel = new JPanel(new BorderLayout());

        JPanel trackingButtons = new JPanel(new GridLayout(1, 2));
        trackingButtons.add(components.getStartTrackingButton());
        trackingButtons.add(components.getStopTrackingButton());

        JPanel settingsButtons = new JPanel(new GridLayout(1, 4));
        settingsButtons.add(components.getClearHistoryButton());
        settingsButtons.add(components.getGlobalSettingsButton());
        settingsButtons.add(components.getExportExcelButton());

        JPanel topPanel = new JPanel(new GridLayout(5, 1));
        topPanel.add(components.getBranchLabel());
        topPanel.add(components.getElapsedTimeLabel());
        topPanel.add(trackingButtons, BorderLayout.AFTER_LAST_LINE);
        topPanel.add(settingsButtons, BorderLayout.AFTER_LAST_LINE);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(components.getSessionTable().getTableScrollPane(), BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return panel;
    }
}
