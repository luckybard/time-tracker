package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.service.SessionService;

import javax.swing.*;

public class SendToJiraButton extends ColumnButtonEditor {

    private final BranchTimeTrackerService branchTimeTrackerService;

    public SendToJiraButton(SessionService sessionService, Runnable reloadTable, BranchTimeTrackerService branchTimeTrackerService) {
        super(sessionService, new JCheckBox(), "Send to Jira", reloadTable);
        this.branchTimeTrackerService = branchTimeTrackerService;
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            boolean success = branchTimeTrackerService.sendSessionToJira(session);
            if (success) {
                session.setSentToJira(true);
                getReloadTable().run();
            } else {
                JOptionPane.showMessageDialog(null, "Błąd podczas wysyłania do JIRA.");
            }
        }
    }
}
