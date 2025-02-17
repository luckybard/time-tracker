package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;

public class TimeTrackerController {
    private final BranchTimeTrackerService trackerService;
    private final TimeTrackerComponents components;
    private final Timer uiUpdateTimer;

    public TimeTrackerController(BranchTimeTrackerService trackerService, TimeTrackerComponents components) {
        this.trackerService = trackerService;
        this.components = components;

        components.getStopTrackingButton().addActionListener(e -> stopTracking());
        components.getClearHistoryButton().addActionListener(e -> clearHistory());

        uiUpdateTimer = new Timer(1000, e -> updateUI());
        uiUpdateTimer.start();
    }

    private void updateUI() {
        String currentBranch = trackerService.getCurrentBranch();
        components.getBranchLabel().setText("Aktualnie śledzony branch: " + (currentBranch != null ? currentBranch : "Brak"));

        Instant startTime = trackerService.getStartTime();
        if (startTime != null) {
            Duration elapsed = Duration.between(startTime, Instant.now());
            components.getElapsedTimeLabel().setText(String.format("Czas: %02d:%02d:%02d",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart()));
        } else {
            components.getElapsedTimeLabel().setText("Czas: 00:00:00");
        }

        updateSessionTable();
    }

    private void updateSessionTable() {
        components.getSessionTable().updateTable();
    }

    private void stopTracking() {
        trackerService.stopTimer();
        updateSessionTable();
        JOptionPane.showMessageDialog(null, "Czas wysłany do JIRA!");
    }

    private void clearHistory() {
        trackerService.clearSessionHistory();
        JOptionPane.showMessageDialog(null, "Historia została wyczyszczona!");
        components.getSessionTable().clearTable();
    }
}