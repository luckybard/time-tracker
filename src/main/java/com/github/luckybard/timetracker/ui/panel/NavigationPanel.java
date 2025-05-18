package com.github.luckybard.timetracker.ui.panel;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.ui.ComponentsProvider.*;

public class NavigationPanel {

    public static JPanel prepareNavigationPanel() {
        JPanel trackingButtons = prepareTrackingButtons();
        JPanel settingsButtons = prepareSettingsButtons();

        JPanel navigationPanel = new JPanel(new GridLayout(5, 1));
        navigationPanel.add(getNameLabel());
        navigationPanel.add(getBranchLabel());
        navigationPanel.add(getElapsedTimeLabel());
        navigationPanel.add(trackingButtons, BorderLayout.AFTER_LAST_LINE);
        navigationPanel.add(settingsButtons, BorderLayout.AFTER_LAST_LINE);
        return navigationPanel;
    }

    private static JPanel prepareSettingsButtons() {
        JPanel settingsButtons = new JPanel(new GridLayout(1, 4));
        settingsButtons.add(getClearHistoryButton());
        settingsButtons.add(getGlobalSettingsButton());
        settingsButtons.add(getEditCurrentSessionButton());
        settingsButtons.add(getExportButton());
        return settingsButtons;
    }

    private static JPanel prepareTrackingButtons() {
        JPanel trackingButtons = new JPanel(new GridLayout(1, 2));
        trackingButtons.add(getStartTrackingButton());
        trackingButtons.add(getStopTrackingButton());
        return trackingButtons;
    }
}
