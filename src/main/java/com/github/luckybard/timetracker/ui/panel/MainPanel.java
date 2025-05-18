package com.github.luckybard.timetracker.ui.panel;

import com.github.luckybard.timetracker.controller.*;
import com.github.luckybard.timetracker.ui.table.TrackerTable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static com.github.luckybard.timetracker.ui.ComponentsProvider.*;
import static com.github.luckybard.timetracker.ui.panel.NavigationPanel.prepareNavigationPanel;
import static com.github.luckybard.timetracker.util.Dictionary.COLON_WITH_SPACE;
import static com.github.luckybard.timetracker.util.Dictionary.translate;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MainPanel {

    private final JPanel panel;
    private final TrackerTable sessionTable;

    private final ExcelController excelController;
    private final TrackerController trackerController;
    private final SessionController sessionController;
    private final PropertiesController propertiesController;

    public MainPanel(@NotNull Project project) {
        this.excelController = project.getService(ExcelController.class);
        this.trackerController = project.getService(TrackerController.class);
        this.sessionController = project.getService(SessionController.class);
        this.propertiesController = project.getService(PropertiesController.class);
        this.sessionTable = new TrackerTable(project);
        this.panel = new JPanel(new BorderLayout());

        initializePanel();
        addButtonsListener();
        initializeUIUpdater(project);
    }

    public JPanel getContent() {
        return panel;
    }

    private void initializePanel() {
        panel.add(prepareNavigationPanel(), BorderLayout.NORTH);
        panel.add(prepareSessionTablePanel(), BorderLayout.CENTER);
    }

    private JScrollPane prepareSessionTablePanel() {
        return sessionTable.getTableScrollPane();
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
}
