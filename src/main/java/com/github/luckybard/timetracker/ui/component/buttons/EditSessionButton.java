package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.util.TimeUtils;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.util.TimeUtils.isEndTimeBeforeStartTime;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EditSessionButton extends ColumnButtonEditor {
    public EditSessionButton(SessionService sessionService, Runnable reloadTable) {
        super(sessionService, new JCheckBox(), "Edit", reloadTable);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);
        if (session != null) {
            JTextField branchField = new JTextField(session.getBranch());
            JTextField dateField = new JTextField(session.getDate());
            JTextField startTimeField = new JTextField(session.getStartTime());
            JTextField endTimeField = new JTextField(session.getEndTime());

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Branch:"));
            panel.add(branchField);
            panel.add(new JLabel("Date (yyyy-MM-dd):"));
            panel.add(dateField);
            panel.add(new JLabel("Start Time (HH:mm):"));
            panel.add(startTimeField);
            panel.add(new JLabel("End Time (HH:mm):"));
            panel.add(endTimeField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                boolean shouldSave = true;
                String branch = branchField.getText();
                String date = dateField.getText();
                String startTime = startTimeField.getText();
                String endTime = endTimeField.getText();

                if (isEmpty(branch) || isEmpty(date) || isEmpty(startTime) || isEmpty(endTime)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (!TimeUtils.isValidDate(date)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (!TimeUtils.isValidTime(startTime) || !TimeUtils.isValidTime(endTime)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, "Invalid time format. Please use HH:mm.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (isEndTimeBeforeStartTime(startTime, endTime)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, "Invalid time. End time cannot be before start time", "Input Error", JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if(shouldSave){
                    session.setBranch(branch);
                    session.setDate(date);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                    getReloadTable().run();
                }
            }
        }
    }
}
