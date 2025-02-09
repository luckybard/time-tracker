package com.github.luckybard.timetracker.ui.component;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class TimeTrackerComponents {
    private final JLabel branchLabel = new JLabel("Aktualny śledzony branch: -");
    private final JLabel elapsedTimeLabel = new JLabel("Czas: 00:00:00");
    private final JButton stopTrackingButton = new JButton("Zatrzymaj i wyślij do JIRA");
    private final JButton clearHistoryButton = new JButton("Wyczyść historię"); // Nowy przycisk
    private final TimeTrackerTable sessionTable;

    public TimeTrackerComponents(Project project) {
        sessionTable = new TimeTrackerTable(project);
    }

    public JLabel getBranchLabel() {
        return branchLabel;
    }

    public JLabel getElapsedTimeLabel() {
        return elapsedTimeLabel;
    }

    public JButton getStopTrackingButton() {
        return stopTrackingButton;
    }

    public JButton getClearHistoryButton() {
        return clearHistoryButton;
    }

    public TimeTrackerTable getSessionTable() {
        return sessionTable;
    }
}
