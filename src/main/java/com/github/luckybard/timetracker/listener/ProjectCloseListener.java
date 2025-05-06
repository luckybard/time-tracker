package com.github.luckybard.timetracker.listener;

import com.github.luckybard.timetracker.service.TrackerService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectCloseListener implements ProjectManagerListener {

    private static final Logger logger = LoggerFactory.getLogger(ProjectCloseListener.class);

    @Override
    public void projectClosing(@NotNull Project project) {
        logger.debug("ProjectCloseListener::projectClosing()");
        TrackerService service = project.getService(TrackerService.class);
        service.stopTimer();
    }
}
