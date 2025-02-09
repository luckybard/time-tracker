package com.github.luckybard.timetracker.listener;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
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
            System.out.println("Brak aktywnego brancha lub HEAD jest odłączony.");
            return;
        }

        if (!newBranch.equals(currentBranch)) {
            trackerService.stopTimer(); // Zatrzymanie poprzedniej sesji

            System.out.println("Zmieniono branch na: " + newBranch);
            currentBranch = newBranch;
            trackerService.startTimer(currentBranch); // Rozpoczęcie nowej sesji
        }
    }

    public static void register(@NotNull Project project) {
        BranchTimeTrackerService trackerService = project.getService(BranchTimeTrackerService.class);
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GitRepository.GIT_REPO_CHANGE, new BranchChangeListener(trackerService));
    }
}