package com.github.luckybard.timetracker.ui.table;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.JiraService;
import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.openapi.project.Project;

import javax.swing.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

public class SendToJiraButton extends ColumnButtonEditor {

    private final JiraService jiraService;
    private final SessionService sessionService;

    public SendToJiraButton(Project project) {
        super(project, new JCheckBox(), translate("send"));
        this.jiraService = project.getService(JiraService.class);
        this.sessionService = project.getService(SessionService.class);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            boolean success = jiraService.sendSessionToJira(session);
            if (success) {
                session.setSentToJira(true);
            } else {
                JOptionPane.showMessageDialog(null, translate("jira.error.request"));
            }
        }
    }
}
