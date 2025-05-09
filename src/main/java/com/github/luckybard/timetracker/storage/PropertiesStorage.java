package com.github.luckybard.timetracker.storage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "PropertiesStorage",
        storages = @Storage("PropertiesStorage.xml")
)
public class PropertiesStorage implements PersistentStateComponent<PropertiesStorage.State> {

    public static class State {
        @Tag("properties")
        public Map<String, String> properties = new HashMap<>();
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
        return state.properties.get(key);
    }

    public Map<String, String> getProperties() {
        return state.properties;
    }
}
