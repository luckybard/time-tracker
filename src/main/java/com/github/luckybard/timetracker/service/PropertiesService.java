package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Properties;
import com.github.luckybard.timetracker.storage.PropertiesStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ObjectUtils.nullizeIfDefaultValue;

@Service(Service.Level.PROJECT)
public final class PropertiesService {

    public static final String JIRA_URL_KEY = "jiraUrl";
    public static final String JIRA_API_TOKEN_KEY = "jiraApiToken";
    public static final String JIRA_USERNAME_KEY = "jiraUsername";
    public static final String JIRA_PROJECT_KEY = "jiraProjectKey";

    private static final Logger logger = LoggerFactory.getLogger(PropertiesService.class);

    private PropertiesStorage.State state;

    public PropertiesService(@NotNull Project project) {
       this.state  = project.getService(PropertiesStorage.class).getState();
    }
    
    public Properties getProperties() {
        return state.properties;
    }

    public String getJiraUrl() {
        return nullizeIfDefaultValue(getProperties().getProperty(JIRA_URL_KEY), StringUtils.EMPTY);
    }

    public String getJiraApiToken() {
        return nullizeIfDefaultValue(getProperties().getProperty(JIRA_API_TOKEN_KEY), StringUtils.EMPTY);
    }

    public String getJiraUsername() {
        return nullizeIfDefaultValue(getProperties().getProperty(JIRA_USERNAME_KEY), StringUtils.EMPTY);
    }

    public String getJiraProjectKey() {
        return nullizeIfDefaultValue(getProperties().getProperty(JIRA_PROJECT_KEY), StringUtils.EMPTY);
    }

    public void setJiraUrlKey(String jiraUrl) {
        getProperties().setProperty(JIRA_URL_KEY, jiraUrl);
    }

    public void setJiraApiTokenKey(String apiToken) {
        getProperties().setProperty(JIRA_API_TOKEN_KEY, apiToken);
    }

    public void setJiraUsernameKey(String username) {
        getProperties().setProperty(JIRA_USERNAME_KEY, username);
    }

    public void setJiraProjectKey(String projectKey) {
        getProperties().setProperty(JIRA_PROJECT_KEY, projectKey);
    }

    public void changeSettings() {
        JTextField url = new JTextField(getJiraUrl());
        JTextField token = new JTextField(getJiraApiToken());
        JTextField username = new JTextField(getJiraUsername());
        JTextField projectKey = new JTextField(getJiraProjectKey());

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

    private void updateConfiguration(String jiraUrl, String apiToken, String username, String projectKey) {
        logger.info("PluginPropertiesService::updateConfiguration()");
        setJiraUrlKey(jiraUrl);
        setJiraApiTokenKey(apiToken);
        setJiraUsernameKey(username);
        setJiraProjectKey(projectKey);
    }
}
