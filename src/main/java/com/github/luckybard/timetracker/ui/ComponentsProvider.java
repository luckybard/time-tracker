package com.github.luckybard.timetracker.ui;

import javax.swing.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;


public final class ComponentsProvider {

    private static final JLabel branchLabel = new JLabel(translate("branch"));
    private static final JLabel nameLabel = new JLabel(translate("name"));
    private static final JLabel elapsedTimeLabel = new JLabel(translate("time"));
    private static final JButton stopTrackingButton = new JButton(translate("tracking.stop"));
    private static final JButton startTrackingButton = new JButton(translate("tracking.start"));
    private static final JButton editCurrentSessionButton = new JButton(translate("edit"));
    private static final JButton clearHistoryButton = new JButton(translate("session.all.clear"));
    private static final JButton globalSettingsButton = new JButton(translate("settings"));
    private static final JButton exportButton = new JButton(translate("export"));

    public static  JLabel getBranchLabel() {
        return branchLabel;
    }

    public static  JLabel getNameLabel() {
        return nameLabel;
    }

    public static  JLabel getElapsedTimeLabel() {
        return elapsedTimeLabel;
    }

    public static  JButton getStopTrackingButton() {
        return stopTrackingButton;
    }

    public static  JButton getStartTrackingButton() {
        return startTrackingButton;
    }

    public static  JButton getEditCurrentSessionButton() {
        return editCurrentSessionButton;
    }

    public static  JButton getClearHistoryButton() {
        return clearHistoryButton;
    }

    public static  JButton getGlobalSettingsButton() {
        return globalSettingsButton;
    }

    public static  JButton getExportButton() {
        return exportButton;
    }
}
