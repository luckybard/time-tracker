package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.repository.SessionStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Service(Service.Level.PROJECT)
public final class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final SessionStorage.State state;

    public SessionService(@NotNull Project project) {
        this.state = project.getService(SessionStorage.class).getState();
    }

    public List<Session> getSessions() {
        return state.sessions;
    }

    public Session getSessionById(String sessionId) {
        return getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public void addSession(Session session) {
        logger.info("SessionService:addSession(), {}", session);
        state.sessions.add(session);
    }

    public void clearSessions() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure about clearing whole history?",
                "Clear session history", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            state.sessions.clear();
            JOptionPane.showMessageDialog(null, "History has been cleared.");
        }
    }
}
