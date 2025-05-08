package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.controller.SessionController;
import com.github.luckybard.timetracker.util.TimeUtils;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.util.TimeUtils.isEndTimeBeforeStartTime;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EditSessionButton extends ColumnButtonEditor {
    public EditSessionButton(SessionController sessionController, Runnable reloadTable) {
        super(sessionController, new JCheckBox(), "Edit", reloadTable);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionController.getSessionById(sessionId);
        if (session != null && !session.isSentToJira()) {
            JTextField branchField = new JTextField(session.getBranch());
            JTextField nameField = new JTextField(session.getName());
            JTextField dateField = new JTextField(session.getDate());
            JTextField startTimeField = new JTextField(session.getStartTime());
            JTextField endTimeField = new JTextField(session.getEndTime());

            Dimension smallFieldSize = new Dimension(150, 25);
            branchField.setPreferredSize(smallFieldSize);
            nameField.setPreferredSize(smallFieldSize);
            dateField.setPreferredSize(smallFieldSize);
            startTimeField.setPreferredSize(smallFieldSize);
            endTimeField.setPreferredSize(smallFieldSize);

            JTextArea descriptionArea = new JTextArea(session.getDescription(), 5, 20);
            descriptionArea.setFont(descriptionArea.getFont());
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descriptionScrollPane = new JBScrollPane(descriptionArea);
            descriptionScrollPane.setPreferredSize(new Dimension(300, 150));

            JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
            fieldsPanel.add(new JLabel("Branch:"));
            fieldsPanel.add(branchField);
            fieldsPanel.add(new JLabel("Name:"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("Date (yyyy-MM-dd):"));
            fieldsPanel.add(dateField);
            fieldsPanel.add(new JLabel("Start Time (HH:mm):"));
            fieldsPanel.add(startTimeField);
            fieldsPanel.add(new JLabel("End Time (HH:mm):"));
            fieldsPanel.add(endTimeField);

            JPanel descriptionPanel = new JPanel(new BorderLayout(5, 5));
            descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
            descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(fieldsPanel, BorderLayout.NORTH);
            mainPanel.add(descriptionPanel, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(null, mainPanel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                boolean shouldSave = true;
                String branch = branchField.getText();
                String name = nameField.getText();
                String description = descriptionArea.getText();
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

                if (shouldSave) {
                    session.setBranch(branch);
                    session.setName(name);
                    session.setDescription(description);
                    session.setDate(date);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                    getReloadTable().run();
                }
            }
        }
    }
}
