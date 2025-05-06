package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.PluginProperties;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@Service(Service.Level.PROJECT)
public final class PropertiesService {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesService.class);

    private final PluginProperties pluginProperties;

    public PropertiesService(@NotNull Project project) {
        this.pluginProperties = project.getService(PluginProperties.class);
    }

    public PluginProperties getPluginProperties() {
        return pluginProperties.getState();
    }

    public void changeSettings() {
        PluginProperties pluginProperties = getPluginProperties();

        JTextField url = new JTextField(pluginProperties.getJiraUrl());
        JTextField token = new JTextField(pluginProperties.getApiToken());
        JTextField username = new JTextField(pluginProperties.getUsername());
        JTextField projectKey = new JTextField(pluginProperties.getProjectKey());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Url:"));
        panel.add(url);
        panel.add(new JLabel("Token:"));
        panel.add(token);
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("ProjectKey:"));
        panel.add(projectKey);
        panel.setPreferredSize(new Dimension(300, 150));

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Edit Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            updateConfiguration(url.getText(), token.getText(), username.getText(), projectKey.getText());
        }
    }

    public void updateConfiguration(String jiraUrl, String apiToken, String username, String projectKey) {
        logger.info("PluginPropertiesService::updateConfiguration()");
        pluginProperties.setJiraUrl(jiraUrl);
        pluginProperties.setApiToken(apiToken);
        pluginProperties.setUsername(username);
        pluginProperties.setProjectKey(projectKey);
    }
}
