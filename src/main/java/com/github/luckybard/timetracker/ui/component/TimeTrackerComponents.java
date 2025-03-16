package com.github.luckybard.timetracker.ui.component;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class TimeTrackerComponents {
    private final JLabel nameLabel = new JLabel("Current session: -");
    private final JLabel elapsedTimeLabel = new JLabel("Time: 00:00:00");
    private final JButton stopTrackingButton = new JButton("Stop tracking");
    private final JButton startTrackingButton = new JButton("Start tracking");
    private final JButton clearHistoryButton = new JButton("Clear all history");
    private final JButton globalSettingsButton = new JButton("Settings");
    private final JButton exportButton = new JButton("Export");
    private final TimeTrackerTable sessionTable;

    public TimeTrackerComponents(Project project) {
        sessionTable = new TimeTrackerTable(project);
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public JLabel getElapsedTimeLabel() {
        return elapsedTimeLabel;
    }

    public JButton getStopTrackingButton() {
        return stopTrackingButton;
    }

    public JButton getStartTrackingButton() {
        return startTrackingButton;
    }

    public JButton getClearHistoryButton() {
        return clearHistoryButton;
    }

    public JButton getGlobalSettingsButton() {
        return globalSettingsButton;
    }

    public JButton getExportButton() {
        return exportButton;
    }

    public TimeTrackerTable getSessionTable() {
        return sessionTable;
    }
}
