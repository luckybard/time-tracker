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

/**
 * Klasa przechowująca listę sesji w sposób trwały.
 */
@State(
        name = "SessionStorage",
        storages = {@Storage("SessionStorage.xml")}
)
public class SessionStorage implements PersistentStateComponent<SessionStorage.State> {

    public static class State {
        // Używamy XCollection do przechowywania listy obiektów Session
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

    /**
     * Zwraca listę zapisanych sesji.
     */
    public List<Session> getSessions() {
        return state.sessions;
    }

    /**
     * Dodaje nową sesję do przechowywanego stanu.
     *
     * @param session Sesja do dodania.
     */
    public void addSession(Session session) {
        state.sessions.add(session);
    }
}