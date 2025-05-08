package com.github.luckybard.timetracker.listener;

import com.github.luckybard.timetracker.controller.TrackerController;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

public class BranchChangeListener implements GitRepositoryChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(BranchChangeListener.class);

    private final TrackerController trackerController;
    private String currentBranch;

    public BranchChangeListener(TrackerController trackerController) {
        this.trackerController = trackerController;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        logger.debug("BranchChangeListener::repositoryChanged()");
        String newBranch = repository.getCurrentBranchName();

        if (newBranch == null) {
            return;
        }

        if (isFalse(newBranch.equals(currentBranch))) {
            trackerController.stopTimer();
            currentBranch = newBranch;
            trackerController.startTimer();
        }
    }

    public static void register(@NotNull Project project) {
        logger.debug("BranchChangeListener::register()");
        TrackerController trackerController = project.getService(TrackerController.class);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GitRepository.GIT_REPO_CHANGE, new BranchChangeListener(trackerController));
    }
}