package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.storage.PropertiesStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.intellij.util.ObjectUtils.nullizeIfDefaultValue;

@Service(Service.Level.PROJECT)
public final class PropertiesService {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesService.class);

    public static final String JIRA_URL_KEY = "jiraUrl";
    public static final String JIRA_API_TOKEN_KEY = "jiraApiToken";
    public static final String JIRA_USERNAME_KEY = "jiraUsername";
    public static final String JIRA_PROJECT_KEY = "jiraProjectKey";

    private final PropertiesStorage storage;

    public PropertiesService(@NotNull Project project) {
        this.storage = project.getService(PropertiesStorage.class);
    }

    public String getJiraUrl() {
        return nullizeIfDefaultValue(storage.getProperty(JIRA_URL_KEY), StringUtils.EMPTY);
    }

    public String getJiraApiToken() {
        return nullizeIfDefaultValue(storage.getProperty(JIRA_API_TOKEN_KEY), StringUtils.EMPTY);
    }

    public String getJiraUsername() {
        return nullizeIfDefaultValue(storage.getProperty(JIRA_USERNAME_KEY), StringUtils.EMPTY);
    }

    public String getJiraProjectKey() {
        return nullizeIfDefaultValue(storage.getProperty(JIRA_PROJECT_KEY), StringUtils.EMPTY);
    }

    public void setJiraUrlKey(String jiraUrl) {
        storage.getProperties().put(JIRA_URL_KEY, jiraUrl);
    }

    public void setJiraApiTokenKey(String apiToken) {
        storage.getProperties().put(JIRA_API_TOKEN_KEY, apiToken);
    }

    public void setJiraUsernameKey(String username) {
        storage.getProperties().put(JIRA_USERNAME_KEY, username);
    }

    public void setJiraProjectKey(String projectKey) {
        storage.getProperties().put(JIRA_PROJECT_KEY, projectKey);
    }

    public void updateConfiguration(String jiraUrl, String apiToken, String username, String projectKey) {
        logger.info("PropertiesService::updateConfiguration()");
        setJiraUrlKey(jiraUrl);
        setJiraApiTokenKey(apiToken);
        setJiraUsernameKey(username);
        setJiraProjectKey(projectKey);
    }
}
