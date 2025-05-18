package com.github.luckybard.timetracker.ui;

import com.github.luckybard.timetracker.ui.panel.MainPanel;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class TimeTrackerToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MainPanel panel = new MainPanel(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel.getContent(), StringUtils.EMPTY, false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(ToolWindow toolWindow) {
        toolWindow.setIcon(IconLoader.getIcon("/icons/logo.svg", getClass()));
    }
}
