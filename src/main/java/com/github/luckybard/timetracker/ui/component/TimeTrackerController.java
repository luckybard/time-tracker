package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.PluginProperties;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.service.PluginPropertiesService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class TimeTrackerController {

    private final BranchTimeTrackerService trackerService;
    private final TimeTrackerComponents components;
    private final Timer uiUpdateTimer;
    private final Project project;

    public TimeTrackerController(@NotNull Project project, BranchTimeTrackerService trackerService, TimeTrackerComponents components) {
        this.trackerService = trackerService;
        this.components = components;
        this.project = project;

        components.getStopTrackingButton().addActionListener(e -> stopTracking());
        components.getStartTrackingButton().addActionListener(e -> startTracking());
        components.getClearHistoryButton().addActionListener(e -> clearHistory());
        components.getGlobalSettingsButton().addActionListener(e -> openGlobalSettings());

        uiUpdateTimer = new Timer(1000, e -> updateUI());
        uiUpdateTimer.start();
    }

    private void updateUI() {
        String currentBranch = trackerService.getCurrentBranch();
        components.getBranchLabel().setText("Aktualnie śledzony branch: " + (currentBranch != null ? currentBranch : "Brak"));

        Instant startTime = trackerService.getStartTime();
        if (startTime != null) {
            components.getStartTrackingButton().setEnabled(false);
            components.getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            components.getElapsedTimeLabel().setText(String.format("Czas: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            components.getElapsedTimeLabel().setText("Czas: 00:00:00");
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
        JOptionPane.showMessageDialog(null, "Sesja zakończona");
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(null, "Czy jesteś pewien?", "Wyczyść historię", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            trackerService.clearSessionHistory();
            JOptionPane.showMessageDialog(null, "Historia została wyczyszczona!");
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
}
