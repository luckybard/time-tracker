package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Properties;
import com.github.luckybard.timetracker.storage.PropertiesStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.util.ObjectUtils.nullizeIfDefaultValue;

@Service(Service.Level.PROJECT)
public final class PropertiesService {

    public static final String JIRA_URL_KEY = "jiraUrl";
    public static final String JIRA_API_TOKEN_KEY = "jiraApiToken";
    public static final String JIRA_USERNAME_KEY = "jiraUsername";
    public static final String JIRA_PROJECT_KEY = "jiraProjectKey";

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
//        getProperties().setProperty(JIRA_PROJECT_KEY, projectKey);
//    }

    public void updateConfiguration(String jiraUrl, String apiToken, String username, String projectKey) {
        logger.info("PluginPropertiesService::updateConfiguration()");
        service.setJiraUrlKey(jiraUrl);
        service.setJiraApiTokenKey(apiToken);
        service.setJiraUsernameKey(username);
        service.setJiraProjectKey(projectKey);
    }
}
