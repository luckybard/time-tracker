package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.controller.*;
import com.github.luckybard.timetracker.ui.component.TimeTrackerTable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TimeTrackerPanel {
    private final JPanel panel;
    private final TimeTrackerTable sessionTable;

    private final ExcelController excelController;
    private final TrackerController trackerController;
    private final SessionController sessionController;
    private final PropertiesController propertiesController;
    private final ComponentsController componentsController;

    public TimeTrackerPanel(@NotNull Project project) {
        this.excelController = project.getService(ExcelController.class);
        this.trackerController = project.getService(TrackerController.class);
        this.sessionController = project.getService(SessionController.class);
        this.componentsController = project.getService(ComponentsController.class);
        this.propertiesController = project.getService(PropertiesController.class);

        sessionTable = new TimeTrackerTable(project);
        panel = new JPanel(new BorderLayout());

        JPanel trackingButtons = new JPanel(new GridLayout(1, 2));

        trackingButtons.add(componentsController.getStartTrackingButton());
        trackingButtons.add(componentsController.getStopTrackingButton());

        JPanel settingsButtons = new JPanel(new GridLayout(1, 4));
        settingsButtons.add(componentsController.getClearHistoryButton());
        settingsButtons.add(componentsController.getGlobalSettingsButton());
        settingsButtons.add(componentsController.getEditCurrentSessionButton());
        settingsButtons.add(componentsController.getExportButton());

        JPanel topPanel = new JPanel(new GridLayout(5, 1));
        topPanel.add(componentsController.getNameLabel());
        topPanel.add(componentsController.getBranchLabel());
        topPanel.add(componentsController.getElapsedTimeLabel());
        topPanel.add(trackingButtons, BorderLayout.AFTER_LAST_LINE);
        topPanel.add(settingsButtons, BorderLayout.AFTER_LAST_LINE);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(sessionTable.getTableScrollPane(), BorderLayout.CENTER);

        componentsController.getStopTrackingButton().addActionListener(e -> trackerController.stopTracking());
        componentsController.getStartTrackingButton().addActionListener(e -> trackerController.startTracking());
        componentsController.getClearHistoryButton().addActionListener(e -> sessionController.clearSessions());
        componentsController.getEditCurrentSessionButton().addActionListener(e -> trackerController.editCurrentSession());
        componentsController.getGlobalSettingsButton().addActionListener(e -> propertiesController.changeSettings());
        componentsController.getExportButton().addActionListener(e -> excelController.promptUserAndExport());

        Timer uiUpdateTimer = new Timer(1000, e -> updateUI());
        uiUpdateTimer.start();
    }

    public JPanel getContent() {
        return panel;
    }

    private void updateUI() {
        checkForLazyUI();
        String name = trackerController.getName();
        componentsController.getNameLabel().setText("Name: " + (isNotBlank(name) ? name : EMPTY));
        String currentBranch = trackerController.getBranch();
        componentsController.getBranchLabel().setText("Session: " + (isNotBlank(currentBranch) ? currentBranch : EMPTY));

        Instant startTime = trackerController.getStartTime();
        if (startTime != null) {
            componentsController.getStartTrackingButton().setEnabled(false);
            componentsController.getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            componentsController.getElapsedTimeLabel().setText(String.format("Time: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            componentsController.getElapsedTimeLabel().setText("Time:");
        }

        sessionTable.updateTable();
    }

    private void checkForLazyUI() {
        if (componentsController.getStartTrackingButton().isEnabled() && componentsController.getStopTrackingButton().isEnabled()) {
            trackerController.startTracking();
        }
    }
}
