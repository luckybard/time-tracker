package com.github.luckybard.timetracker.ui.table;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.ui.table.button.DeleteSessionButton;
import com.github.luckybard.timetracker.ui.table.button.EditSessionButton;
import com.github.luckybard.timetracker.ui.table.button.SendToJiraButton;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.Duration;
import java.util.List;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

public class TrackerTable {

    private final JBTable table;
    private final DefaultTableModel tableModel;
    private final SessionService sessionService;

    public TrackerTable(@NotNull Project project) {
        this.sessionService = project.getService(SessionService.class);
        this.tableModel = new DefaultTableModel(new String[]{
                translate("id"),
                translate("branch"),
                translate("name"),
                translate("date"),
                translate("start.time"),
                translate("end.time"),
                translate("duration"),
                translate("jira.send"),
                translate("edit"),
                translate("delete"),
        }, 0);
        this.table = new JBTable(tableModel);

        updateTable(project);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
        tableModel.fireTableDataChanged();
        table.clearSelection();
    }

    public JScrollPane getTableScrollPane() {
        return new JBScrollPane(table);
    }

    public void updateTable(@NotNull Project project) {
        int selectedRowBeforeUpdate = table.getSelectedRow();

        SwingUtilities.invokeLater(() -> {
            clearTable();
            List<Session> sessions = sessionService.getSessions();
            for (Session session : sessions) {
                tableModel.addRow(new Object[]{
                        session.getId(),
                        session.getBranch(),
                        session.getName(),
                        session.getDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        formatDuration(session.getDuration()),
                        session.isSentToJira() ? translate("sent") : translate("send"),
                        session.isSentToJira() ? translate("jira.session.already.sent") : translate("edit"),
                        translate("delete")
                });
            }

            initializeButtons(project);

            if (selectedRowBeforeUpdate >= 0 && selectedRowBeforeUpdate < tableModel.getRowCount()) {
                table.setRowSelectionInterval(selectedRowBeforeUpdate, selectedRowBeforeUpdate);
            } else {
                table.clearSelection();
            }
        });
    }

    private void initializeButtons(@NotNull Project project) {
        table.getColumn(translate("jira.send")).setCellRenderer(new ButtonRenderer());
        table.getColumn(translate("jira.send")).setCellEditor(new SendToJiraButton(project));

        table.getColumn(translate("edit")).setCellRenderer(new ButtonRenderer());
        table.getColumn(translate("edit")).setCellEditor(new EditSessionButton(project));

        table.getColumn(translate("delete")).setCellRenderer(new ButtonRenderer());
        table.getColumn(translate("delete")).setCellEditor(new DeleteSessionButton(project, tableModel));
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}
