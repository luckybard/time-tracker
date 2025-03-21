package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.service.TimeTrackerService;

import javax.swing.*;

public class SendToJiraButton extends ColumnButtonEditor {

    private final TimeTrackerService timeTrackerService;

    public SendToJiraButton(SessionService sessionService, Runnable reloadTable, TimeTrackerService timeTrackerService) {
        super(sessionService, new JCheckBox(), "Send", reloadTable);
        this.timeTrackerService = timeTrackerService;
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            boolean success = timeTrackerService.sendSessionToJira(session);
            if (success) {
                session.setSentToJira(true);
                getReloadTable().run();
            } else {
                JOptionPane.showMessageDialog(null, "Error while sending request to Jira:");
            }
        }
    }
}
