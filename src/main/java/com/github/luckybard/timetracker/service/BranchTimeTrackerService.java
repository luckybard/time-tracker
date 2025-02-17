package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.config.JiraClient;
import com.github.luckybard.timetracker.config.PluginProperties;
import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.repository.SessionStorage;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Service(Service.Level.PROJECT)
public final class BranchTimeTrackerService {

    private static final Logger log = LoggerFactory.getLogger(BranchTimeTrackerService.class);
    private final Project project;
    private String branch;
    private Instant startTime;
    private JiraClient jiraClient;

    public BranchTimeTrackerService(@NotNull Project project) {
        this.project = project;
        this.jiraClient = new JiraClient(project);
    }

    public SessionStorage getSessionStorage() {
        return project.getService(SessionStorage.class);
    }

    public PluginProperties getSettings(){
        return project.getService(PluginProperties.class);
    }

    public void startTimer(String currentBranch) {
        System.out.println("BranchTimeTrackerService::startTimer");
        branch = currentBranch;
        startTime = Instant.now();
    }

    public void stopTimer() {
        if (isFalse(branch == null || branch.isEmpty())) {
            getSessionStorage().addSession(getSession());
        }
        branch = null;
        startTime = null;
    }

    public Session getSession() {
        Session session = new Session()
                .setId(UUID.randomUUID().toString())
                .setBranch(branch)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setSentToJira(false);

        return session;
    }

    public boolean sendSessionToJira(Session session) {
        try {
            String timeSpent = session.getDurationAsString();
            String comment = "Sesja rozpoczęła się o " + session.getStartTime() +
                    ", zakończyła o " + session.getEndTime() +
                    ", czas trwania: " + timeSpent;


            return jiraClient.sendJiraUpdate(getIssueKey(session), timeSpent, comment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getIssueKey(Session session) {
        return getSettings().getProjectKey() + "-" + session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
    }

    public void clearSessionHistory() {
        getSessionStorage().clearSessions();
    }

    public String getCurrentBranch() {
        return branch;
    }

    public Instant getStartTime() {
        return startTime;
    }
}