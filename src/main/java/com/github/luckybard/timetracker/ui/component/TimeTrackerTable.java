package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.ui.component.buttons.ButtonRenderer;
import com.github.luckybard.timetracker.ui.component.buttons.DeleteSessionButton;
import com.github.luckybard.timetracker.ui.component.buttons.EditSessionButton;
import com.github.luckybard.timetracker.ui.component.buttons.SendToJiraButton;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.Duration;
import java.util.List;

public class TimeTrackerTable {
    private final JBTable table;
    private final DefaultTableModel tableModel;
    private final BranchTimeTrackerService trackerService;

    public TimeTrackerTable(Project project) {
        this.trackerService = project.getService(BranchTimeTrackerService.class);
        tableModel = new DefaultTableModel(new String[]{"ID", "Branch", "Date", "Start Time", "End Time", "Duration", "Send to Jira", "Edit", "Delete"}, 0);
        table = new JBTable(tableModel);

        updateTable();
    }

    public void updateTable() {
        clearTable();
        List<Session> sessions = trackerService.getSessionStorage().getSessions();
        for (Session session : sessions) {
            tableModel.addRow(new Object[]{
                    session.getId(),
                    session.getBranch(),
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    formatDuration(session.getDuration()),
                    session.isSentToJira() ? "Sent to Jira" : "Send to Jira",
                    "Edit",
                    "Delete"
            });
        }
        initializeButtons();
    }

    public void initializeButtons() {
        table.getColumn("Send to Jira").setCellRenderer(new ButtonRenderer());
        table.getColumn("Send to Jira").setCellEditor(new SendToJiraButton(trackerService, this::updateTable));

        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new EditSessionButton(trackerService, this::updateTable));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new DeleteSessionButton(trackerService, this::updateTable, tableModel, table));
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public JScrollPane getTableScrollPane() {
        return new JBScrollPane(table);
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}
