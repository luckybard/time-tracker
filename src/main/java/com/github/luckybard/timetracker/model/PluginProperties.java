package com.github.luckybard.timetracker.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
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

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
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
