package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
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
        BranchTimeTrackerService trackerService = project.getService(BranchTimeTrackerService.class);
        this.components = new TimeTrackerComponents();
        this.controller = new TimeTrackerController(trackerService, components);

        panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(4, 1));
        topPanel.add(components.getBranchLabel());
        topPanel.add(components.getElapsedTimeLabel());
        topPanel.add(components.getStopTrackingButton());
        topPanel.add(components.getClearHistoryButton());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(components.getSessionTable().getTableScrollPane(), BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return panel;
    }
}