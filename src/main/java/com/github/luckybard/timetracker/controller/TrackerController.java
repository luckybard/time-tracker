package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.service.TrackingService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

import static com.github.luckybard.timetracker.ui.ComponentsProvider.*;
import static com.github.luckybard.timetracker.util.Dictionary.translate;

@Service(Service.Level.PROJECT)
public final class TrackerController {

    private final TrackingService trackingService;

    public TrackerController(@NotNull Project project) {
        this.trackingService = project.getService(TrackingService.class);
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
        fieldsPanel.add(new JLabel(translate("name")));
        fieldsPanel.add(nameField);

        JTextArea descriptionArea = new JTextArea(trackingService.getDescription(), 5, 20);
        descriptionArea.setFont(descriptionArea.getFont());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JBScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel descriptionPanel = new JPanel(new BorderLayout(1, 1));
        descriptionPanel.add(new JLabel(translate("description")), BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, mainPanel, translate("session.edit"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            trackingService.editCurrentSession(nameField.getText(), descriptionArea.getText());
        }
    }

    public void startTracking() {
        trackingService.startTimer();
        getStartTrackingButton().setEnabled(false);
        getStopTrackingButton().setEnabled(true);
        getEditCurrentSessionButton().setEnabled(true);
    }

    public void stopTracking() {
        trackingService.stopTimer();
        getStartTrackingButton().setEnabled(true);
        getStopTrackingButton().setEnabled(false);
        getEditCurrentSessionButton().setEnabled(false);
        JOptionPane.showMessageDialog(null, translate("tracking.session.stopped"));
    }
}