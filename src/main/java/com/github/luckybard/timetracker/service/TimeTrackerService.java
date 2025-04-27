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

import static com.github.luckybard.timetracker.util.TimeUtils.getDurationAsString;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Service(Service.Level.PROJECT)
public final class TimeTrackerService {

    private static final Logger logger = LoggerFactory.getLogger(TimeTrackerService.class);

    private String branch;
    private String name;
    private String description;
    private Instant startTime;
    private final JiraClient jiraClient;
    private final SessionService sessionService;
    private final PluginPropertiesService pluginPropertiesService;
    private final Project project;

    public TimeTrackerService(@NotNull Project project) {
        this.jiraClient = project.getService(JiraClient.class);
        this.sessionService = project.getService(SessionService.class);
        this.pluginPropertiesService = project.getService(PluginPropertiesService.class);
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public TimeTrackerService setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TimeTrackerService setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getBranch() {
        return branch;
    }

    public TimeTrackerService setBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public void startTimer() {
        String currentBranch = fetchCurrentBranch();
        logger.info("TimeTrackerService::startTimer(), starting timer for branch: {}", currentBranch);
        this.branch = currentBranch;
        this.name = currentBranch;
        this.startTime = Instant.now();
    }

    public void stopTimer() {
        logger.info("TimeTrackerService::stopTimer(), stopping timer for branch: {}", fetchCurrentBranch());
        if (isBranchValid()) {
            sessionService.addSession(createSession());
        }
        resetTimer();
    }

    private boolean isBranchValid() {
        return branch != null && !branch.isEmpty();
    }

    private void resetTimer() {
        this.name = null;
        this.branch = null;
        this.startTime = null;
    }

    private Session createSession() {
        String id = UUID.randomUUID().toString();
        logger.info("TimeTrackerService::createSession(), creating session with id : {}", id);
        return new Session()
                .setId(id)
                .setBranch(branch)
                .setName(name)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setSentToJira(false);
    }

    public boolean sendSessionToJira(Session session) {
        try {
            String timeSpent = getDurationAsString(session.getDuration());
            String comment = generateComment(session, timeSpent);
            return jiraClient.sendJiraUpdate(getIssueKey(session), timeSpent, comment);
        } catch (Exception e) {
            logger.error("Failed to send session to Jira", e);
            return false;
        }
    }

    private String generateComment(Session session, String timeSpent) {
        String comment = String.format("Session started at %s, ended at %s, duration: %s",
                session.getStartTime(), session.getEndTime(), timeSpent);

        if (StringUtils.isNotBlank(session.getName()) && isFalse(session.getName().equals(session.getBranch()))) {
            comment = comment + SPACE + String.format("named: %s ", session.getName());
        }
        if (StringUtils.isNotBlank(session.getDescription())) {
            comment = comment + SPACE + String.format("with description: %s ", session.getDescription());
        }

        return comment;
    }

    private String getIssueKey(Session session) {
        return pluginPropertiesService.getPluginProperties().getProjectKey() + "-" +
                session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
    }

    public void clearSessionHistory() {
        logger.info("TimeTrackerService::clearSessionHistory()");
        sessionService.clearSessions();
    }

    public String fetchCurrentBranch() {
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

    public boolean isTracking() {
        return startTime != null;
    }
}