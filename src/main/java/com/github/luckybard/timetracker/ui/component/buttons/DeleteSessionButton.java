package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteSessionButton extends ColumnButtonEditor {
    private final DefaultTableModel tableModel;
    private final JBTable table;

    public DeleteSessionButton(BranchTimeTrackerService trackerService, Runnable reloadTable, DefaultTableModel tableModel, JBTable table) {
        super(new JCheckBox(), "Delete", trackerService, reloadTable);
        this.tableModel = tableModel;
        this.table = table;
    }

    @Override
    public void handleButtonClick(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        String sessionId = (String) tableModel.getValueAt(row, 0);
        Session session = getTrackerService().getSessionById (sessionId);

        if (session != null) {
            if (confirmAction("Czy na pewno chcesz usunąć?", "Usuwanie")) {
                getTrackerService().getSessionStorage().getSessions().remove(session);
            }
        }
    }
}
