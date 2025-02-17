package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;

import javax.swing.*;

public class SendToJiraButton extends ColumnButtonEditor {
    public SendToJiraButton(BranchTimeTrackerService trackerService, Runnable reloadTable) {
        super(new JCheckBox(), "Send to Jira", trackerService, reloadTable);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = getTrackerService().getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            boolean success = getTrackerService().sendSessionToJira(session);
            if (success) {
                session.setSentToJira(true);
                getReloadTable().run();
            } else {
                JOptionPane.showMessageDialog(null, "Błąd podczas wysyłania do JIRA.");
            }
        }
    }
}
