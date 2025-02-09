package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.repository.SessionStorage;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service(Service.Level.PROJECT)
public final class BranchTimeTrackerService {

    private final  Project project;
    private String branch;
    private Instant startTime;

    public BranchTimeTrackerService(@NotNull Project project) {
        this.project = project;
    }

    private SessionStorage getSessionStorage() {
        return project.getService(SessionStorage.class);
    }

    public void startTimer(String currentBranch) {
        branch = currentBranch;
        startTime = Instant.now();
    }

    public void stopTimer() {
        getSessionStorage().addSession(getSession());

        //TODO: Send request to JIRA
    }

    public Session getSession() {
        Session session = new Session()
                .setId(Long.valueOf(UUID.randomUUID().toString()))
                .setBranch(branch)
                .setStartTime(startTime.toString())
                .setEndTime(Instant.now().toString())
                .setDate(LocalDate.now().toString());

        startTime = null;
        return session;
    }

    public void isBranchDifferent(String currentBranch) {
        if (!currentBranch.equals(branch)) {
            stopTimer();
        }
    }
}