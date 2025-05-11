package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.service.PropertiesService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

@Service(Service.Level.PROJECT)
public final class PropertiesController {

    private final PropertiesService service;

    public PropertiesController(@NotNull Project project) {
        this.service = project.getService(PropertiesService.class);
    }

    public void changeSettings() {
        JTextField url = new JTextField(service.getJiraUrl());
        JTextField token = new JTextField(service.getJiraApiToken());
        JTextField username = new JTextField(service.getJiraUsername());
        JTextField projectKey = new JTextField(service.getJiraProjectKey());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel(translate("url")));
        panel.add(url);
        panel.add(new JLabel(translate("token")));
        panel.add(token);
        panel.add(new JLabel(translate("username")));
        panel.add(username);
        panel.add(new JLabel(translate("projectkey")));
        panel.add(projectKey);
        panel.setPreferredSize(new Dimension(300, 150));

        int result = JOptionPane.showConfirmDialog(null, panel,
                translate("edit.settings"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            service.updateConfiguration(url.getText(), token.getText(), username.getText(), projectKey.getText());
        }
    }


}
