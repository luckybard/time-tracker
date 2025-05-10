package com.github.luckybard.timetracker.listener;

import com.github.luckybard.timetracker.controller.TrackerController;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BranchChangeListener implements GitRepositoryChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(BranchChangeListener.class);

    private final TrackerController trackerController;

    public BranchChangeListener(@NotNull Project project) {
        this.trackerController = project.getService(TrackerController.class);
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        logger.debug("BranchChangeListener::repositoryChanged()");
        String newBranch = repository.getCurrentBranchName();
        if (newBranch == null) {
            return;
        }

        boolean isTracking = trackerController.getStartTime() != null;
        if (isTracking && trackerController.getBranch() != null && !trackerController.getBranch().equals(StringUtils.EMPTY)) {
            trackerController.stopTracking();
        }

        trackerController.startTracking();
    }
}
