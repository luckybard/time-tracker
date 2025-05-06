package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.service.*;
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

    private final ExcelService excelService;
    private final TrackerService trackerService;
    private final SessionService sessionService;
    private final PropertiesService propertiesService;
    private final ComponentsProviderService componentsProviderService;

    public TimeTrackerPanel(@NotNull Project project) {
        this.excelService = project.getService(ExcelService.class);
        this.trackerService = project.getService(TrackerService.class);
        this.sessionService = project.getService(SessionService.class);
        this.componentsProviderService = project.getService(ComponentsProviderService.class);
        this.propertiesService = project.getService(PropertiesService.class);

        sessionTable = new TimeTrackerTable(project);
        panel = new JPanel(new BorderLayout());

        JPanel trackingButtons = new JPanel(new GridLayout(1, 2));

        trackingButtons.add(componentsProviderService.getStartTrackingButton());
        trackingButtons.add(componentsProviderService.getStopTrackingButton());

        JPanel settingsButtons = new JPanel(new GridLayout(1, 4));
        settingsButtons.add(componentsProviderService.getClearHistoryButton());
        settingsButtons.add(componentsProviderService.getGlobalSettingsButton());
        settingsButtons.add(componentsProviderService.getEditCurrentSessionButton());
        settingsButtons.add(componentsProviderService.getExportButton());

        JPanel topPanel = new JPanel(new GridLayout(5, 1));
        topPanel.add(componentsProviderService.getNameLabel());
        topPanel.add(componentsProviderService.getBranchLabel());
        topPanel.add(componentsProviderService.getElapsedTimeLabel());
        topPanel.add(trackingButtons, BorderLayout.AFTER_LAST_LINE);
        topPanel.add(settingsButtons, BorderLayout.AFTER_LAST_LINE);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(sessionTable.getTableScrollPane(), BorderLayout.CENTER);

        componentsProviderService.getStopTrackingButton().addActionListener(e -> trackerService.stopTracking());
        componentsProviderService.getStartTrackingButton().addActionListener(e -> trackerService.startTracking());
        componentsProviderService.getClearHistoryButton().addActionListener(e -> sessionService.clearSessions());
        componentsProviderService.getEditCurrentSessionButton().addActionListener(e -> trackerService.editCurrentSession());
        componentsProviderService.getGlobalSettingsButton().addActionListener(e -> propertiesService.changeSettings());
        componentsProviderService.getExportButton().addActionListener(e -> excelService.promptUserAndExport());

        Timer uiUpdateTimer = new Timer(1000, e -> updateUI());
        uiUpdateTimer.start();
    }

    public JPanel getContent() {
        return panel;
    }

    private void updateUI() {
        checkForLazyUI();
        String name = trackerService.getName();
        componentsProviderService.getNameLabel().setText("Name: " + (isNotBlank(name) ? name : EMPTY));
        String currentBranch = trackerService.getBranch();
        componentsProviderService.getBranchLabel().setText("Session: " + (isNotBlank(currentBranch) ? currentBranch : EMPTY));

        Instant startTime = trackerService.getStartTime();
        if (startTime != null) {
            componentsProviderService.getStartTrackingButton().setEnabled(false);
            componentsProviderService.getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            componentsProviderService.getElapsedTimeLabel().setText(String.format("Time: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            componentsProviderService.getElapsedTimeLabel().setText("Time:");
        }

        sessionTable.updateTable();
    }

    private void checkForLazyUI() {
        if (componentsProviderService.getStartTrackingButton().isEnabled() && componentsProviderService.getStopTrackingButton().isEnabled()) {
            trackerService.startTracking();
        }
    }
}
