package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.config.JiraClient;
import com.github.luckybard.timetracker.config.PluginProperties;
import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.repository.SessionStorage;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Service(Service.Level.PROJECT)
public final class BranchTimeTrackerService {

    private static final Logger log = LoggerFactory.getLogger(BranchTimeTrackerService.class);

    private String branch;
    private Instant startTime;
    private JiraClient jiraClient;
    private PluginProperties pluginProperties;
    private SessionStorage sessionStorage;

    public BranchTimeTrackerService(@NotNull Project project) {
        this.jiraClient = project.getService(JiraClient.class);
        this.pluginProperties = project.getService(PluginProperties.class);
        this.sessionStorage = project.getService(SessionStorage.class);
    }

    public SessionStorage getSessionStorage() {
        return sessionStorage;
    }

    public PluginProperties getPluginProperties() {
        return pluginProperties;
    }

    public void startTimer(String currentBranch) {
        log.info("Starting timer for branch: {}", currentBranch);
        this.branch = currentBranch;
        this.startTime = Instant.now();
    }

    public void stopTimer() {
        if (isBranchValid()) {
            sessionStorage.addSession(createSession());
        }
        resetTimer();
    }

    private boolean isBranchValid() {
        return branch != null && !branch.isEmpty();
    }

    private void resetTimer() {
        this.branch = null;
        this.startTime = null;
    }

    private Session createSession() {
        return new Session()
                .setId(UUID.randomUUID().toString())
                .setBranch(branch)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setSentToJira(false);
    }

    public boolean sendSessionToJira(Session session) {
        try {
            String timeSpent = session.getDurationAsString();
            String comment = generateComment(session, timeSpent);
            return jiraClient.sendJiraUpdate(getIssueKey(session), timeSpent, comment);
        } catch (Exception e) {
            log.error("Failed to send session to Jira", e);
            return false;
        }
    }

    private String generateComment(Session session, String timeSpent) {
        return String.format("Session started at %s, ended at %s, duration: %s",
                session.getStartTime(), session.getEndTime(), timeSpent);
    }

    private String getIssueKey(Session session) {
        return pluginProperties.getProjectKey() + "-" + session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
    }

    public Session getSessionById(String sessionId) {
        return getSessionStorage().getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public void clearSessionHistory() {
        sessionStorage.clearSessions();
    }

    public String getCurrentBranch() {
        return branch;
    }

    public Instant getStartTime() {
        return startTime;
    }
}