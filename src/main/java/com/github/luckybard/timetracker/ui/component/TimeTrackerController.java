package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.service.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class TimeTrackerController {

    private final ExcelService excelService;
    private final TrackerService trackerService;
    private final SessionService sessionService;
    private final PropertiesService propertiesService;
    private final ComponentsProviderService componentsProviderService;

    public TimeTrackerController(@NotNull Project project) {
        this.excelService = project.getService(ExcelService.class);
        this.trackerService = project.getService(TrackerService.class);
        this.sessionService = project.getService(SessionService.class);
        this.componentsProviderService = project.getService(ComponentsProviderService.class);
        this.propertiesService = project.getService(PropertiesService.class);

        initializeButtons();
    }

    private void initializeButtons() {

    }


}
