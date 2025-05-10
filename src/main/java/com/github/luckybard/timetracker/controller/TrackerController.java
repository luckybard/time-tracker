package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.service.TrackingService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

@Service(Service.Level.PROJECT)
public final class TrackerController {

    private static final Logger logger = LoggerFactory.getLogger(TrackerController.class);

    private final ComponentsController componentsController;
    private final TrackingService trackingService;

    public TrackerController(@NotNull Project project) {
        this.componentsController = project.getService(ComponentsController.class);
        this.trackingService = project.getService(TrackingService.class);
    }

    public String getDescription() {
        return trackingService.getDescription();
    }

    public String getBranch() {
        return trackingService.getBranch();
    }

    public String getName() {
        return trackingService.getName();
    }

    public Instant getStartTime() {
        return trackingService.getStartTime();
    }

    public void editCurrentSession() {
        Dimension smallFieldSize = new Dimension(150, 25);
        JTextField nameField = new JTextField(trackingService.getName());
        nameField.setPreferredSize(smallFieldSize);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);

        JTextArea descriptionArea = new JTextArea(trackingService.getDescription(), 5, 20);
        descriptionArea.setFont(descriptionArea.getFont());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JBScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel descriptionPanel = new JPanel(new BorderLayout(1, 1));
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, mainPanel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            trackingService.setName(nameField.getText());
            trackingService.setDescription(descriptionArea.getText());
        }
    }

    public void startTracking() {
        trackingService.startTimer();
        componentsController.getStartTrackingButton().setEnabled(false);
        componentsController.getStopTrackingButton().setEnabled(true);
        componentsController.getEditCurrentSessionButton().setEnabled(true);
    }

    public void stopTracking() {
        trackingService.stopTimer();
        componentsController.getStartTrackingButton().setEnabled(true);
        componentsController.getStopTrackingButton().setEnabled(false);
        componentsController.getEditCurrentSessionButton().setEnabled(false);
        JOptionPane.showMessageDialog(null, "Session has been stopped.");
    }
}