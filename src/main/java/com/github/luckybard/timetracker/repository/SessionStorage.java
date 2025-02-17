package com.github.luckybard.timetracker.repository;


import com.github.luckybard.timetracker.model.Session;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.XCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "SessionStorage",
        storages = {@Storage("SessionStorage.xml")}
)
public class SessionStorage implements PersistentStateComponent<SessionStorage.State> {

    public static class State {
        @XCollection(elementName = "session")
        public List<Session> sessions = new ArrayList<>();
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

    public List<Session> getSessions() {
        return state.sessions;
    }

    public void addSession(Session session) {
        state.sessions.add(session);
    }

    public void clearSessions() {
        state.sessions.clear();
    }
}