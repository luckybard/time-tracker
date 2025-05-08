package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.controller.SessionController;
import com.github.luckybard.timetracker.controller.TrackerController;

import javax.swing.*;

public class SendToJiraButton extends ColumnButtonEditor {

    private final TrackerController trackerController;

    public SendToJiraButton(SessionController sessionController, Runnable reloadTable, TrackerController trackerController) {
        super(sessionController, new JCheckBox(), "Send", reloadTable);
        this.trackerController = trackerController;
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionController.getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            boolean success = trackerController.sendSessionToJira(session);
            if (success) {
                session.setSentToJira(true);
                getReloadTable().run();
            } else {
                JOptionPane.showMessageDialog(null, "Error while sending request to Jira:");
            }
        }
    }
}
