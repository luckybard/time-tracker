package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.PluginProperties;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.service.ExcelExporterService;
import com.github.luckybard.timetracker.service.PluginPropertiesService;
import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class TimeTrackerController {

    private final ExcelExporterService excelExporterService;
    private final BranchTimeTrackerService trackerService;
    private final SessionService sessionService;
    private final TimeTrackerComponents components;
    private final Timer uiUpdateTimer;
    private final Project project;

    public TimeTrackerController(ExcelExporterService excelExporterService, @NotNull Project project, BranchTimeTrackerService trackerService, SessionService sessionService, TimeTrackerComponents components) {
        this.excelExporterService = excelExporterService;
        this.trackerService = trackerService;
        this.sessionService = sessionService;
        this.components = components;
        this.project = project;

        initializeButtons(components);

        uiUpdateTimer = new Timer(1000, e -> updateUI());
        uiUpdateTimer.start();
    }

    private void initializeButtons(TimeTrackerComponents components) {
        components.getStopTrackingButton().addActionListener(e -> stopTracking());
        components.getStartTrackingButton().addActionListener(e -> startTracking());
        components.getClearHistoryButton().addActionListener(e -> clearHistory());
        components.getGlobalSettingsButton().addActionListener(e -> openGlobalSettings());
        components.getExportButton().addActionListener(e -> {
            try {
                exportToExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void updateUI() {
        String currentBranch = trackerService.getCurrentBranch();
        components.getNameLabel().setText("Current session: " + (currentBranch != null ? currentBranch : StringUtils.EMPTY));

        Instant startTime = trackerService.getStartTime();
        if (startTime != null) {
            components.getStartTrackingButton().setEnabled(false);
            components.getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            components.getElapsedTimeLabel().setText(String.format("Time: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            components.getElapsedTimeLabel().setText("Time: 00:00:00");
        }

        updateSessionTable();
    }

    private void updateSessionTable() {
        components.getSessionTable().updateTable();
    }

    private void startTracking() {
        trackerService.startTimer(trackerService.getCurrentBranch());
        components.getStartTrackingButton().setEnabled(false);
        components.getStopTrackingButton().setEnabled(true);
        updateSessionTable();
    }

    private void stopTracking() {
        trackerService.stopTimer();
        components.getStartTrackingButton().setEnabled(true);
        components.getStopTrackingButton().setEnabled(false);
        updateSessionTable();
        JOptionPane.showMessageDialog(null, "Session has been stopped.");
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure about clearing whole history?", "Clear session history", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            trackerService.clearSessionHistory();
            JOptionPane.showMessageDialog(null, "History has been cleared.");
            components.getSessionTable().clearTable();
        }
    }

    private void openGlobalSettings() {
        PluginPropertiesService propertiesService = project.getService(PluginPropertiesService.class);
        PluginProperties pluginProperties = propertiesService.getPluginProperties();

        JTextField url = new JTextField(pluginProperties.getJiraUrl());
        JTextField token = new JTextField(pluginProperties.getApiToken());
        JTextField username = new JTextField(pluginProperties.getUsername());
        JTextField projectKey = new JTextField(pluginProperties.getProjectKey());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Url:"));
        panel.add(url);
        panel.add(new JLabel("Token:"));
        panel.add(token);
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("ProjectKey:"));
        panel.add(projectKey);
        panel.setPreferredSize(new Dimension(300, 150));

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Edit Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            propertiesService.updateConfiguration(url.getText(), token.getText(), username.getText(), projectKey.getText());
        }
    }

    private void exportToExcel() throws IOException {
        excelExporterService.promptUserAndExport(sessionService.getSessions());
    }
}
