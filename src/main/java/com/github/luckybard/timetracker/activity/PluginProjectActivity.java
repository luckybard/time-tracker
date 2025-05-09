package com.github.luckybard.timetracker.activity;

import com.github.luckybard.timetracker.controller.TrackerController;
import com.github.luckybard.timetracker.listener.BranchChangeListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginProjectActivity implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        startTracking(project);
        registerBranchChangeListener(project);
        return null;
    }

    private void startTracking(@NotNull Project project) {
        TrackerController service = project.getService(TrackerController.class);
        service.startTracking();
    }

    private void registerBranchChangeListener(@NotNull Project project) {
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(GitRepository.GIT_REPO_CHANGE, new BranchChangeListener(project));
    }
}
