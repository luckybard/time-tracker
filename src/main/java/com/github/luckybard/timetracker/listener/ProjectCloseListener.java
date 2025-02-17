package com.github.luckybard.timetracker.listener;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class ProjectCloseListener implements ProjectManagerListener {

    @Override
    public void projectClosing(@NotNull Project project) {
        BranchTimeTrackerService service = project.getService(BranchTimeTrackerService.class);
        service.stopTimer();
    }
}
