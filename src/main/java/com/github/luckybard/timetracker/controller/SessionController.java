package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.storage.SessionStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.github.luckybard.timetracker.util.Dictionary.translate;

@Service(Service.Level.PROJECT)
public final class SessionController {

    private final SessionStorage.State state;

    public SessionController(@NotNull Project project) {
        this.state = project.getService(SessionStorage.class).getState();
    }

    public void clearSessions() {
        int confirm = JOptionPane.showConfirmDialog(null, translate("session.history.clear.confirm"),
                "session.history.clear.title", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            state.sessions.clear();
            JOptionPane.showMessageDialog(null, translate("session.history.cleared"));
        }
    }
}
