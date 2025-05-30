package com.github.luckybard.timetracker.ui.table.button;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

public class DeleteSessionButton extends ColumnButtonEditor {
    private final DefaultTableModel tableModel;
    private final SessionService sessionService;

    public DeleteSessionButton(Project project, DefaultTableModel tableModel) {
        super(project, new JCheckBox(), translate("delete"));
        this.tableModel = tableModel;
        this.sessionService = project.getService(SessionService.class);
    }

    @Override
    public void handleButtonClick(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        String sessionId = (String) tableModel.getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);

        if (session != null) {
            if (confirmAction(translate("session.delete.confirm"), translate("session.delete"))) {
                sessionService.removeSession(session);
            }
        }
    }
}
