package com.github.luckybard.timetracker.controller;

import com.intellij.openapi.components.Service;

import javax.swing.*;

@Service(Service.Level.PROJECT)
public final class ComponentsController {
    private final JLabel branchLabel = new JLabel("Branch:");
    private final JLabel nameLabel = new JLabel("Name:");
    private final JLabel elapsedTimeLabel = new JLabel("Time:");
    private final JButton stopTrackingButton = new JButton("Stop tracking");
    private final JButton startTrackingButton = new JButton("Start tracking");
    private final JButton editCurrentSessionButton = new JButton("Edit");
    private final JButton clearHistoryButton = new JButton("Clear all history");
    private final JButton globalSettingsButton = new JButton("Settings");
    private final JButton exportButton = new JButton("Export");

    public JLabel getBranchLabel() {
        return branchLabel;
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

    public JButton getEditCurrentSessionButton() {
        return editCurrentSessionButton;
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
}
