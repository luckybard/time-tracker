package com.github.luckybard.timetracker.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "PluginProperties",
        storages = @Storage("plugin-properties.xml")
)
public class PluginProperties implements PersistentStateComponent<PluginProperties> {

    public String jiraUrl;
    public String apiToken;
    public String username;
    public String projectKey;

    public String getJiraUrl() {
        return jiraUrl;
    }

    public PluginProperties setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
        return this;
    }

    public String getApiToken() {
        return apiToken;
    }

    public PluginProperties setApiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PluginProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public PluginProperties setProjectKey(String projectKey) {
        this.projectKey = projectKey;
        return this;
    }

    @Override
    public @Nullable PluginProperties getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginProperties pluginProperties) {
        this.jiraUrl = pluginProperties.jiraUrl;
        this.apiToken = pluginProperties.apiToken;
        this.username = pluginProperties.username;
        this.projectKey = pluginProperties.projectKey;
    }
}
