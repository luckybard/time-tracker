package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.config.JiraClient;
import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Service(Service.Level.PROJECT)
public final class BranchTimeTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(BranchTimeTrackerService.class);

    private String branch;
    private Instant startTime;
    private final JiraClient jiraClient;
    private final SessionService sessionService;
    private final PluginPropertiesService pluginPropertiesService;
    private final Project project;

    public BranchTimeTrackerService(@NotNull Project project) {
        this.jiraClient = project.getService(JiraClient.class);
        this.sessionService = project.getService(SessionService.class);
        this.pluginPropertiesService = project.getService(PluginPropertiesService.class);
        this.project = project;
    }

    public void startTimer(String currentBranch) {
        logger.info("BranchTimeTrackerService::startTimer(), starting timer for branch: {}", currentBranch);
        this.branch = currentBranch;
        this.startTime = Instant.now();
    }

    public void stopTimer() {
        logger.info("BranchTimeTrackerService::stopTimer(), stopping timer for branch: {}", getCurrentBranch());
        if (isBranchValid()) {
            sessionService.addSession(createSession());
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
        String id = UUID.randomUUID().toString();
        logger.info("BranchTimeTrackerService::createSession(), creating session with id : {}", id);
        return new Session()
                .setId(id)
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
            logger.error("Failed to send session to Jira", e);
            return false;
        }
    }

    private String generateComment(Session session, String timeSpent) {
        return String.format("Session started at %s, ended at %s, duration: %s",
                session.getStartTime(), session.getEndTime(), timeSpent);
    }

    private String getIssueKey(Session session) {
        return pluginPropertiesService.getPluginProperties().getProjectKey() + "-" +
                session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
    }

    public void clearSessionHistory() {
        logger.info("BranchTimeTrackerService::clearSessionHistory()");
        sessionService.clearSessions();
    }

    public String getCurrentBranch() {
        GitRepository gitRepository = GitRepositoryManager.getInstance(project).getRepositories().stream()
                .findFirst()
                .orElse(null);

        if (gitRepository != null) {
            return gitRepository.getCurrentBranch().getName();
        }
        return StringUtils.EMPTY;
    }

    public Instant getStartTime() {
        return startTime;
    }
}