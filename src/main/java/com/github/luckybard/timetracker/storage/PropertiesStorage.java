package com.github.luckybard.timetracker.storage;

import com.github.luckybard.timetracker.model.Properties;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "PropertiesStorage",
        storages = @Storage("PropertiesStorage.xml")
)
public class PropertiesStorage implements PersistentStateComponent<PropertiesStorage.State> {

    public static class State {
        @Tag("properties")
        public Properties properties = new Properties();
    }

    private State state = new State();

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public String getProperty(String key) {
        return state.properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        state.properties.setProperty(key, value);
    }

    public boolean hasProperty(String key) {
        return state.properties.hasProperty(key);
    }

    public void removeProperty(String key) {
        state.properties.removeProperty(key);
    }

    public Properties getProperties() {
        return state.properties;
    }
}
