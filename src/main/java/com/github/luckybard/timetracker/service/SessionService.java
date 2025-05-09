package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.storage.SessionStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@Service(Service.Level.PROJECT)
public final class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private SessionStorage storage;

    public SessionService(@NotNull Project project) {
        this.storage = project.getService(SessionStorage.class);
    }

    public List<Session> getSessions() {
        return Objects.requireNonNull(getState()).sessions;
    }

    private SessionStorage.State getState() {
        return storage.getState();
    }

    public Session getSessionById(String sessionId) {
        return getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public void saveSession(Session session) {
        logger.info("SessionService:saveSession(), {}", session);
        getSessions().add(session);
    }

    public void removedSession(Session session) {
        logger.info("SessionService:removedSession(), {}", session);
        getSessions().add(session);
    }
}
