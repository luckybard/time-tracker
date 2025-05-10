package com.github.luckybard.timetracker.ui.table;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DeleteSessionButton extends ColumnButtonEditor {
    private final DefaultTableModel tableModel;
    private final SessionService sessionService;

    public DeleteSessionButton(Project project, DefaultTableModel tableModel) {
        super(project, new JCheckBox(), "Delete");
        this.tableModel = tableModel;
        this.sessionService = project.getService(SessionService.class);
    }

    @Override
    public void handleButtonClick(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        String sessionId = (String) tableModel.getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);

        if (session != null) {
            if (confirmAction("Are you sure about deleting this session?", "Delete")) {
                sessionService.removeSession(session);
            }
        }
    }
}
