package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteSessionButton extends ColumnButtonEditor {
    private final DefaultTableModel tableModel;

    public DeleteSessionButton(SessionService sessionService, Runnable reloadTable, DefaultTableModel tableModel) {
        super(sessionService, new JCheckBox(), "Delete", reloadTable);
        this.tableModel = tableModel;
    }

    @Override
    public void handleButtonClick(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        String sessionId = (String) tableModel.getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);

        if (session != null) {
            if (confirmAction("Czy na pewno chcesz usunąć?", "Usuwanie")) {
                sessionService.getSessions().remove(session);
            }
        }
    }
}
