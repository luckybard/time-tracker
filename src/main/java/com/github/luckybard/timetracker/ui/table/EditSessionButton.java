package com.github.luckybard.timetracker.ui.table;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.SessionService;
import com.github.luckybard.timetracker.util.TimeUtils;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;
import static com.github.luckybard.timetracker.util.TimeUtils.isEndTimeBeforeStartTime;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EditSessionButton extends ColumnButtonEditor {

    private final SessionService sessionService;

    public EditSessionButton(Project project) {
        super(project, new JCheckBox(), translate("edit"));
        this.sessionService = project.getService(SessionService.class);
    }

    @Override
    public void handleButtonClick(int row) {
        String sessionId = (String) getTable().getValueAt(row, 0);
        Session session = sessionService.getSessionById(sessionId);
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
            fieldsPanel.add(new JLabel(translate("branch")));
            fieldsPanel.add(branchField);
            fieldsPanel.add(new JLabel(translate("name")));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel(translate("date.format.label")));
            fieldsPanel.add(dateField);
            fieldsPanel.add(new JLabel(translate("start.time.format.label")));
            fieldsPanel.add(startTimeField);
            fieldsPanel.add(new JLabel(translate("end.time.format.label")));
            fieldsPanel.add(endTimeField);

            JPanel descriptionPanel = new JPanel(new BorderLayout(5, 5));
            descriptionPanel.add(new JLabel(translate("description")), BorderLayout.NORTH);
            descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(fieldsPanel, BorderLayout.NORTH);
            mainPanel.add(descriptionPanel, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(null, mainPanel, translate("edit.session"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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
                    JOptionPane.showMessageDialog(null, translate("error.validation.fields"), translate("error.input.title"), JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (!TimeUtils.isValidDate(date)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, translate("error.session.date.format"), translate("error.input.title"), JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (!TimeUtils.isValidTime(startTime) || !TimeUtils.isValidTime(endTime)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, translate("error.session.time.format"), translate("error.input.title"), JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (isEndTimeBeforeStartTime(startTime, endTime)) {
                    shouldSave = false;
                    JOptionPane.showMessageDialog(null, "error.session.time.end", translate("error.input.title"), JOptionPane.ERROR_MESSAGE);
                    this.handleButtonClick(row);
                }

                if (shouldSave) {
                    session.setBranch(branch);
                    session.setName(name);
                    session.setDescription(description);
                    session.setDate(date);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                }
            }
        }
    }
}
