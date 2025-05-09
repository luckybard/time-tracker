package com.github.luckybard.timetracker.service;

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
public final class TrackingService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingService.class);

    private String branch;
    private String name;
    private String description;
    private Instant startTime;
    private final GitRepositoryManager gitRepositoryManager;
    private final SessionService sessionService;

    public TrackingService(@NotNull Project project) {
        this.gitRepositoryManager = GitRepositoryManager.getInstance(project);
        this.sessionService = project.getService(SessionService.class);
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
        logger.info("TimeTrackerService::startTimer()");
        String currentBranch = fetchCurrentBranch();
        this.branch = currentBranch;
        this.name = currentBranch;
        this.startTime = Instant.now();
    }

    public void stopTimer() {
        logger.info("TimeTrackerService::stopTimer(), stopping timer for branch: {}", fetchCurrentBranch());
        if (isBranchValid()) {
            sessionService.saveSession(createSession());
        }
        resetTimer();
    }

    private Session createSession() {
        logger.info("TimeTrackingService::createSession()");
        return new Session()
                .setId(UUID.randomUUID().toString())
                .setBranch(branch)
                .setName(name)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setSentToJira(false);
    }

    private boolean isBranchValid() {
        return branch != null && !branch.isEmpty();
    }

    private void resetTimer() {
        this.name = null;
        this.branch = null;
        this.startTime = null;
    }

    public String fetchCurrentBranch() {
        GitRepository gitRepository = gitRepositoryManager.getRepositories().stream()
                .findFirst()
                .orElse(null);

        if (gitRepository != null && gitRepository.getCurrentBranch() != null) {
            return gitRepository.getCurrentBranch().getName();
        }
        return StringUtils.EMPTY;
    }
}
