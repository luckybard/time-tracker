package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.controller.*;
import com.github.luckybard.timetracker.ui.table.TimeTrackerTable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static com.github.luckybard.timetracker.ui.ComponentsProvider.*;
import static com.github.luckybard.timetracker.util.Dictionary.COLON_WITH_SPACE;
import static com.github.luckybard.timetracker.util.Dictionary.translate;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TimeTrackerPanel {

    private final JPanel panel;
    private final TimeTrackerTable sessionTable;

    private final ExcelController excelController;
    private final TrackerController trackerController;
    private final SessionController sessionController;
    private final PropertiesController propertiesController;

    public TimeTrackerPanel(@NotNull Project project) {
        this.excelController = project.getService(ExcelController.class);
        this.trackerController = project.getService(TrackerController.class);
        this.sessionController = project.getService(SessionController.class);
        this.propertiesController = project.getService(PropertiesController.class);
        this.sessionTable = new TimeTrackerTable(project);
        this.panel = new JPanel(new BorderLayout());

        initializePanel();
        addButtonsListener();
        initializeUIUpdater(project);
    }

    private void initializePanel() {
        panel.add(prepareNavigationPanel(), BorderLayout.NORTH);
        panel.add(prepareSessionTablePanel(), BorderLayout.CENTER);
    }

    private JScrollPane prepareSessionTablePanel() {
        return sessionTable.getTableScrollPane();
    }

    private JPanel prepareNavigationPanel() {
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

    private void initializeUIUpdater(Project project) {
        Timer uiUpdateTimer = new Timer(1000, e -> updateUI(project));
        uiUpdateTimer.start();
    }

    private void addButtonsListener() {
        getStopTrackingButton().addActionListener(e -> trackerController.stopTracking());
        getStartTrackingButton().addActionListener(e -> trackerController.startTracking());
        getClearHistoryButton().addActionListener(e -> sessionController.clearSessions());
        getEditCurrentSessionButton().addActionListener(e -> trackerController.editCurrentSession());
        getGlobalSettingsButton().addActionListener(e -> propertiesController.changeSettings());
        getExportButton().addActionListener(e -> excelController.promptUserAndExport());
    }

    private void updateUI(Project project) {
        checkForLazyUI();
        updateNavigationPanelUI();
        updateSessionTablePanelUI(project);
    }

    private void updateSessionTablePanelUI(Project project) {
        sessionTable.updateTable(project);
    }

    private void updateNavigationPanelUI() {
        String name = trackerController.getName();
        getNameLabel().setText(translate("name") + COLON_WITH_SPACE + (isNotBlank(name) ? name : EMPTY));
        String currentBranch = trackerController.getBranch();
        getBranchLabel().setText(translate("branch") + COLON_WITH_SPACE + (isNotBlank(currentBranch) ? currentBranch : EMPTY));

        Instant startTime = trackerController.getStartTime();
        if (startTime != null) {
            getStartTrackingButton().setEnabled(false);
            getStopTrackingButton().setEnabled(true);
            Duration elapsed = Duration.between(startTime, Instant.now());
            getElapsedTimeLabel().setText(translate("time") + COLON_WITH_SPACE +
                    String.format("%02d:%02d:%02d", elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            getElapsedTimeLabel().setText(translate("time") + COLON_WITH_SPACE);
        }
    }

    private void checkForLazyUI() {
        if (getStartTrackingButton().isEnabled() && getStopTrackingButton().isEnabled()) {
            trackerController.startTracking();
        }
    }

    public JPanel getContent() {
        return panel;
    }
}
