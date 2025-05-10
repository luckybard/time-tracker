package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.storage.SessionStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

@Service(Service.Level.PROJECT)
public final class SessionController {

    private final SessionStorage.State state;

    public SessionController(@NotNull Project project) {
        this.state = project.getService(SessionStorage.class).getState();
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
