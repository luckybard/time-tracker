package com.github.luckybard.timetracker.ui.panel;

import com.github.luckybard.timetracker.controller.*;
import com.github.luckybard.timetracker.ui.table.SessionTable;
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

public class MainPanel {

    private final JPanel panel;
    private final SessionTable sessionTable;
    private final NavigationPanel navigationPanel;

    private final TrackerController trackerController;

    public MainPanel(@NotNull Project project) {
        this.trackerController = project.getService(TrackerController.class);
        this.sessionTable = new SessionTable(project);
        this.panel = new JPanel(new BorderLayout());
        this.navigationPanel = new NavigationPanel(project);

        initializePanel();
        initializeUIUpdater(project);
    }

    public JPanel getContent() {
        return panel;
    }

    private void initializePanel() {
        panel.add(navigationPanel.prepareNavigationPanel(), BorderLayout.NORTH);
        panel.add(prepareSessionTablePanel(), BorderLayout.CENTER);
    }

    private JScrollPane prepareSessionTablePanel() {
        return sessionTable.getTableScrollPane();
    }

    private void initializeUIUpdater(Project project) {
        Timer uiUpdateTimer = new Timer(1000, e -> updateUI(project));
        uiUpdateTimer.start();
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
