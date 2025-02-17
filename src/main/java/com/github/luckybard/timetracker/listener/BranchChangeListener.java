package com.github.luckybard.timetracker.listener;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.github.luckybard.timetracker.ui.TimeTrackerPanel;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;

public class BranchChangeListener implements GitRepositoryChangeListener {

    private final BranchTimeTrackerService trackerService;
    private String currentBranch;

    public BranchChangeListener(BranchTimeTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
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
        BranchTimeTrackerService trackerService = project.getService(BranchTimeTrackerService.class);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GitRepository.GIT_REPO_CHANGE, new BranchChangeListener(trackerService));
    }
}