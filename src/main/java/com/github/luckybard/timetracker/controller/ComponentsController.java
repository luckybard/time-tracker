package com.github.luckybard.timetracker.controller;

import com.intellij.openapi.components.Service;

import javax.swing.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

@Service(Service.Level.PROJECT)
public final class ComponentsController {

    private final JLabel branchLabel = new JLabel(translate("branch"));
    private final JLabel nameLabel = new JLabel(translate("name"));
    private final JLabel elapsedTimeLabel = new JLabel(translate("time"));
    private final JButton stopTrackingButton = new JButton(translate("tracking.stop"));
    private final JButton startTrackingButton = new JButton(translate("tracking.start"));
    private final JButton editCurrentSessionButton = new JButton(translate("edit"));
    private final JButton clearHistoryButton = new JButton(translate("session.all.clear"));
    private final JButton globalSettingsButton = new JButton(translate("settings"));
    private final JButton exportButton = new JButton(translate("export"));

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
