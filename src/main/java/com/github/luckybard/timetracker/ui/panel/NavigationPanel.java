package com.github.luckybard.timetracker.ui.panel;

import com.github.luckybard.timetracker.controller.ExcelController;
import com.github.luckybard.timetracker.controller.PropertiesController;
import com.github.luckybard.timetracker.controller.SessionController;
import com.github.luckybard.timetracker.controller.TrackerController;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.ui.ComponentsProvider.*;

public class NavigationPanel {

    private final ExcelController excelController;
    private final SessionController sessionController;
    private final PropertiesController propertiesController;
    private final TrackerController trackerController;

    public NavigationPanel(@NotNull Project project) {
        this.sessionController = project.getService(SessionController.class);
        this.propertiesController = project.getService(PropertiesController.class);
        this.excelController = project.getService(ExcelController.class);
        this.trackerController = project.getService(TrackerController.class);
    }

    public JPanel prepareNavigationPanel() {
        JPanel trackingButtons = prepareTrackingButtons();
        JPanel settingsButtons = prepareSettingsButtons();

        JPanel navigationPanel = new JPanel(new GridLayout(5, 1));
        navigationPanel.add(getNameLabel());
        navigationPanel.add(getBranchLabel());
        navigationPanel.add(getElapsedTimeLabel());
        navigationPanel.add(trackingButtons, BorderLayout.AFTER_LAST_LINE);
        navigationPanel.add(settingsButtons, BorderLayout.AFTER_LAST_LINE);

        addButtonsListener();
        return navigationPanel;
    }

    private JPanel prepareSettingsButtons() {
        JPanel settingsButtons = new JPanel(new GridLayout(1, 4));
        settingsButtons.add(getClearHistoryButton());
        settingsButtons.add(getGlobalSettingsButton());
        settingsButtons.add(getEditCurrentSessionButton());
        settingsButtons.add(getExportButton());
        return settingsButtons;
    }

    private JPanel prepareTrackingButtons() {
        JPanel trackingButtons = new JPanel(new GridLayout(1, 2));
        trackingButtons.add(getStartTrackingButton());
        trackingButtons.add(getStopTrackingButton());
        return trackingButtons;
    }

    private void addButtonsListener() {
        getStopTrackingButton().addActionListener(e -> trackerController.stopTracking());
        getStartTrackingButton().addActionListener(e -> trackerController.startTracking());
        getClearHistoryButton().addActionListener(e -> sessionController.clearSessions());
        getEditCurrentSessionButton().addActionListener(e -> trackerController.editCurrentSession());
        getGlobalSettingsButton().addActionListener(e -> propertiesController.changeSettings());
        getExportButton().addActionListener(e -> excelController.promptUserAndExport());
    }
}
