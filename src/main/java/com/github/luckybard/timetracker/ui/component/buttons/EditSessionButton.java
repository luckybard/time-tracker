package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;

import javax.swing.*;
import java.awt.*;

public class EditSessionButton extends ColumnButtonEditor {
    public EditSessionButton(BranchTimeTrackerService trackerService, Runnable reloadTable) {
        super(new JCheckBox(), "Edit", trackerService, reloadTable);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = getTrackerService().getSessionById(sessionId);
        if (session != null) {
            JTextField branchField = new JTextField(session.getBranch());
            JTextField dateField = new JTextField(session.getDate());
            JTextField startTimeField = new JTextField(session.getStartTime());
            JTextField endTimeField = new JTextField(session.getEndTime());

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Branch:"));
            panel.add(branchField);
            panel.add(new JLabel("Date:"));
            panel.add(dateField);
            panel.add(new JLabel("Start Time:"));
            panel.add(startTimeField);
            panel.add(new JLabel("End Time:"));
            panel.add(endTimeField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                session.setBranch(branchField.getText());
                session.setDate(dateField.getText());
                session.setStartTime(startTimeField.getText());
                session.setEndTime(endTimeField.getText());
                getReloadTable().run();
            }
        }
    }
}
