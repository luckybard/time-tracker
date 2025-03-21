package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.PluginProperties;
import com.github.luckybard.timetracker.service.ExcelExporterService;
import com.github.luckybard.timetracker.service.PluginPropertiesService;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.service.TimeTrackerService;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TimeTrackerController {

    private final ExcelExporterService excelExporterService;
    private final TimeTrackerService timeTrackerService;
    private final SessionService sessionService;
    private final TimeTrackerComponents components;
    private final Timer uiUpdateTimer;
    private final Project project;

    public TimeTrackerController(ExcelExporterService excelExporterService, @NotNull Project project, TimeTrackerService timeTrackerService, SessionService sessionService, TimeTrackerComponents components) {
        this.excelExporterService = excelExporterService;
        this.timeTrackerService = timeTrackerService;
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
        components.getEditCurrentSessionButton().addActionListener(e -> editCurrentSession());
        components.getGlobalSettingsButton().addActionListener(e -> openGlobalSettings());
        components.getExportButton().addActionListener(e -> {
            try {
                exportToExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void editCurrentSession() {
        Dimension smallFieldSize = new Dimension(150, 25);
        JTextField nameField = new JTextField(timeTrackerService.getName());
        nameField.setPreferredSize(smallFieldSize);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);

        JTextArea descriptionArea = new JTextArea(timeTrackerService.getDescription(), 5, 20);
        descriptionArea.setFont(descriptionArea.getFont());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JBScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel descriptionPanel = new JPanel(new BorderLayout(1, 1));
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, mainPanel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            timeTrackerService.setName(nameField.getText());
            timeTrackerService.setDescription(descriptionArea.getText());
        }
    }

    private void updateUI() {
        checkForLazyUI();
        String name = timeTrackerService.getName();
        components.getNameLabel().setText("Name: " + (isNotBlank(name) ? name : EMPTY));
        String currentBranch = timeTrackerService.getBranch();
        components.getBranchLabel().setText("Session: " + (isNotBlank(currentBranch) ? currentBranch : EMPTY));

        Instant startTime = timeTrackerService.getStartTime();
        if (startTime != null) {
            components.getStartTrackingButton().setEnabled(false);
            components.getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            components.getElapsedTimeLabel().setText(String.format("Time: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            components.getElapsedTimeLabel().setText("Time:");
        }

        updateSessionTable();
    }

    private void checkForLazyUI() {
        if (components.getStartTrackingButton().isEnabled() && components.getStopTrackingButton().isEnabled()) {
            startTracking();
        }
    }

    private void updateSessionTable() {
        components.getSessionTable().updateTable();
    }

    private void startTracking() {
        timeTrackerService.startTimer(timeTrackerService.fetchCurrentBranch());
        components.getStartTrackingButton().setEnabled(false);
        components.getStopTrackingButton().setEnabled(true);
        components.getEditCurrentSessionButton().setEnabled(true);
        updateSessionTable();
    }

    private void stopTracking() {
        timeTrackerService.stopTimer();
        components.getStartTrackingButton().setEnabled(true);
        components.getStopTrackingButton().setEnabled(false);
        components.getEditCurrentSessionButton().setEnabled(false);
        updateSessionTable();
        JOptionPane.showMessageDialog(null, "Session has been stopped.");
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure about clearing whole history?", "Clear session history", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            timeTrackerService.clearSessionHistory();
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
