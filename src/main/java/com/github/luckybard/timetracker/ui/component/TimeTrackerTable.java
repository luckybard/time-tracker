package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.Session;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.Duration;
import java.util.List;

public class TimeTrackerTable {
    private final JTable table;
    private final DefaultTableModel tableModel;

    public TimeTrackerTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Branch", "Date", "Start Time", "End Time", "Duration"}, 0);
        table = new JBTable(tableModel);
    }

    public void updateTable(List<Session> sessions) {
        tableModel.setRowCount(0); // Czyszczenie tabeli
        for (Session session : sessions) {
            tableModel.addRow(new Object[]{
                    session.getId(),
                    session.getBranch(),
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    formatDuration(session.getDuration())
            });
        }
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    public JScrollPane getTableScrollPane() {
        return new JBScrollPane(table);
    }
}
