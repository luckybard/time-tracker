package com.github.luckybard.timetracker.listener;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import groovy.util.logging.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class BranchChangeListener implements GitRepositoryChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(BranchChangeListener.class);

    private final BranchTimeTrackerService trackerService;
    private String currentBranch;

    public BranchChangeListener(BranchTimeTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        logger.debug("BranchChangeListener::repositoryChanged()");
        String newBranch = repository.getCurrentBranchName();

        if (newBranch == null) {
            return;
        }

        if (!newBranch.equals(currentBranch)) {
            trackerService.stopTimer();
            currentBranch = newBranch;
            trackerService.startTimer(currentBranch);
        }
    }

    public static void register(@NotNull Project project) {
        logger.debug("BranchChangeListener::register()");
        BranchTimeTrackerService trackerService = project.getService(BranchTimeTrackerService.class);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GitRepository.GIT_REPO_CHANGE, new BranchChangeListener(trackerService));
    }
}