package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.controller.SessionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteSessionButton extends ColumnButtonEditor {
    private final DefaultTableModel tableModel;

    public DeleteSessionButton(SessionController sessionController, Runnable reloadTable, DefaultTableModel tableModel) {
        super(sessionController, new JCheckBox(), "Delete", reloadTable);
        this.tableModel = tableModel;
    }

    @Override
    public void handleButtonClick(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        String sessionId = (String) tableModel.getValueAt(row, 0);
        Session session = sessionController.getSessionById(sessionId);

        if (session != null) {
            if (confirmAction("Are you sure about deleting this session?", "Delete")) {
                sessionController.getSessions().remove(session);
            }
        }
    }
}
