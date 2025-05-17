package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Service(Service.Level.PROJECT)
public final class TrackingService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingService.class);

    private final SessionService sessionService;
    private final GitService gitService;

    private String branch;
    private String name;
    private String description;
    private Instant startTime;

    public TrackingService(@NotNull Project project) {
        this.sessionService = project.getService(SessionService.class);
        this.gitService = project.getService(GitService.class);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void startTimer() {
        logger.info("TrackingService::startTimer()");
        String currentBranch = gitService.fetchCurrentBranch();
        setBranch(currentBranch);
        setName(currentBranch);
        setStartTime(Instant.now());
    }

    public void stopTimer() {
        logger.info("TrackingService::stopTimer()");
        if (isBranchValid()) {
            sessionService.saveSession(createSession());
        }
        resetTimer();
    }

    private Session createSession() {
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        session.setBranch(branch);
        session.setName(name);
        session.setStartTime(InstantFormatter.formatTime(startTime));
        session.setEndTime(InstantFormatter.formatTime(Instant.now()));
        session.setDate(InstantFormatter.formatDate(Instant.now()));
        session.setSentToJira(false);
        return session;
    }

    private boolean isBranchValid() {
        return branch != null && !branch.isEmpty();
    }

    private void resetTimer() {
        setBranch(null);
        setName(null);
        setStartTime(null);
    }
}
