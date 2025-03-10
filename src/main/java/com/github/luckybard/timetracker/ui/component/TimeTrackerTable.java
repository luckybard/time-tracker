package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.ui.component.buttons.ButtonRenderer;
import com.github.luckybard.timetracker.ui.component.buttons.DeleteSessionButton;
import com.github.luckybard.timetracker.ui.component.buttons.EditSessionButton;
import com.github.luckybard.timetracker.ui.component.buttons.SendToJiraButton;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.Duration;
import java.util.List;

public class TimeTrackerTable {

    private static final Logger logger = LoggerFactory.getLogger(TimeTrackerTable.class);

    private final JBTable table;
    private final DefaultTableModel tableModel;
    private final SessionService sessionService;
    private final BranchTimeTrackerService branchTimeTrackerService;

    public TimeTrackerTable(@NotNull Project project) {
        this.sessionService = project.getService(SessionService.class);
        this.branchTimeTrackerService = project.getService(BranchTimeTrackerService.class);
        this.tableModel = new DefaultTableModel(new String[]{
                "ID",
                "Branch",
                "Date",
                "Start Time",
                "End Time",
                "Duration",
                "Send to Jira",
                "Edit",
                "Delete"
        }, 0);
        this.table = new JBTable(tableModel);

        updateTable();
    }

    public void clearTable() {
        tableModel.setRowCount(0);
        tableModel.fireTableDataChanged();
        table.clearSelection();
    }

    public JScrollPane getTableScrollPane() {
        return new JBScrollPane(table);
    }

    public void updateTable() {
        int selectedRowBeforeUpdate = table.getSelectedRow();

        SwingUtilities.invokeLater(() -> {
            clearTable();
            List<Session> sessions = sessionService.getSessions();
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

            if (selectedRowBeforeUpdate >= 0 && selectedRowBeforeUpdate < tableModel.getRowCount()) {
                table.setRowSelectionInterval(selectedRowBeforeUpdate, selectedRowBeforeUpdate);
            } else {
                table.clearSelection();
            }
        });
    }

    private void initializeButtons() {
        table.getColumn("Send to Jira").setCellRenderer(new ButtonRenderer());
        table.getColumn("Send to Jira").setCellEditor(new SendToJiraButton(sessionService, this::updateTable, branchTimeTrackerService));

        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new EditSessionButton(sessionService, this::updateTable));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new DeleteSessionButton(sessionService, this::updateTable, tableModel));
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}
