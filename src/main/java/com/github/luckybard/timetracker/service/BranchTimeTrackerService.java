package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.repository.SessionStorage;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Service(Service.Level.PROJECT)
public final class BranchTimeTrackerService {

    private List<Session> sessionHistory;

    private static final Logger log = LoggerFactory.getLogger(BranchTimeTrackerService.class);
    private final Project project;
    private String branch;
    private Instant startTime;

    public BranchTimeTrackerService(@NotNull Project project) {
        this.project = project;
    }

    public SessionStorage getSessionStorage() {
        return project.getService(SessionStorage.class);
    }

    public void startTimer(String currentBranch) {
        System.out.println("BranchTimeTrackerService::startTimer");
        branch = currentBranch;
        startTime = Instant.now();
    }

    public void stopTimer() {
        System.out.println("BranchTimeTrackerService::stopTimer");
        if (isFalse(branch == null || branch.isEmpty())) {
            getSessionStorage().addSession(getSession());
            // TODO: Send request to JIRA
        }
        branch = null;
        startTime = null;
    }

    public Session getSession() {
        Session session = new Session()
                .setId(UUID.randomUUID().toString())
                .setBranch(branch)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setDuration();

        return session;
    }

    public void isBranchDifferent(String currentBranch) {
        if (!currentBranch.equals(branch)) {
            stopTimer();
        }
    }

    public void clearSessionHistory() {
       getSessionStorage().clearSessions();
    }

    // Metoda zwracająca aktualny branch
    public String getCurrentBranch() {
        return branch;
    }

    // Metoda zwracająca czas rozpoczęcia sesji
    public Instant getStartTime() {
        return startTime;
    }

    public List<Session> getSessionHistory() {
        return getSessionStorage().getSessions();
    }
}