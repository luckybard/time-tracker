package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.PluginProperties;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(Service.Level.PROJECT)
public final class PluginPropertiesService {

    private static final Logger logger = LoggerFactory.getLogger(PluginPropertiesService.class);

    private final PluginProperties pluginProperties;

    public PluginPropertiesService(@NotNull Project project) {
        this.pluginProperties = project.getService(PluginProperties.class);
    }

    public PluginProperties getPluginProperties() {
        return pluginProperties.getState();
    }

    public void updateConfiguration(String jiraUrl, String apiToken, String username, String projectKey) {
        logger.info("PluginPropertiesService::updateConfiguration()");
        pluginProperties.setJiraUrl(jiraUrl);
        pluginProperties.setApiToken(apiToken);
        pluginProperties.setUsername(username);
        pluginProperties.setProjectKey(projectKey);
    }
}
