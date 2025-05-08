package com.github.luckybard.timetracker.controller;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.JiraService;
import com.github.luckybard.timetracker.util.InstantFormatter;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.UUID;

import static com.github.luckybard.timetracker.util.TimeUtils.getDurationAsString;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Service(Service.Level.PROJECT)
public final class TrackerController {

    private static final Logger logger = LoggerFactory.getLogger(TrackerController.class);

    private String branch;
    private String name;
    private String description;
    private Instant startTime;
    private final JiraService jiraService;
    private final SessionController sessionController;
    private final PropertiesController propertiesController;
    private final ComponentsController componentsController;
    private final Project project;

    public TrackerController(@NotNull Project project) {
        this.jiraService = project.getService(JiraService.class);
        this.sessionController = project.getService(SessionController.class);
        this.propertiesController = project.getService(PropertiesController.class);
        this.componentsController = project.getService(ComponentsController.class);;
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void startTimer() {
        String currentBranch = fetchCurrentBranch();
        logger.info("TimeTrackerService::startTimer(), starting timer for branch: {}", currentBranch);
        this.branch = currentBranch;
        this.name = currentBranch;
        this.startTime = Instant.now();
    }

    public void stopTimer() {
        logger.info("TimeTrackerService::stopTimer(), stopping timer for branch: {}", fetchCurrentBranch());
        if (isBranchValid()) {
            sessionController.addSession(createSession());
        }
        resetTimer();
    }

    private boolean isBranchValid() {
        return branch != null && !branch.isEmpty();
    }

    private void resetTimer() {
        this.name = null;
        this.branch = null;
        this.startTime = null;
    }

    private Session createSession() {
        String id = UUID.randomUUID().toString();
        logger.info("TimeTrackerService::createSession(), creating session with id : {}", id);
        return new Session()
                .setId(id)
                .setBranch(branch)
                .setName(name)
                .setStartTime(InstantFormatter.formatTime(startTime))
                .setEndTime(InstantFormatter.formatTime(Instant.now()))
                .setDate(InstantFormatter.formatDate(Instant.now()))
                .setSentToJira(false);
    }

    public boolean sendSessionToJira(Session session) {
        try {
            String timeSpent = getDurationAsString(session.getDuration());
            String comment = generateComment(session, timeSpent);
            return jiraService.sendJiraUpdate(getIssueKey(session), timeSpent, comment);
        } catch (Exception e) {
            logger.error("Failed to send session to Jira", e);
            return false;
        }
    }

    private String generateComment(Session session, String timeSpent) {
        String comment = String.format("Session started at %s, ended at %s, duration: %s",
                session.getStartTime(), session.getEndTime(), timeSpent);

        if (StringUtils.isNotBlank(session.getName()) && isFalse(session.getName().equals(session.getBranch()))) {
            comment = comment + SPACE + String.format("named: %s ", session.getName());
        }
        if (StringUtils.isNotBlank(session.getDescription())) {
            comment = comment + SPACE + String.format("with description: %s ", session.getDescription());
        }

        return comment;
    }

    private String getIssueKey(Session session) {
        return propertiesController.getJiraProjectKey() + "-" +
                session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
    }

    public String fetchCurrentBranch() {
        GitRepository gitRepository = GitRepositoryManager.getInstance(project).getRepositories().stream()
                .findFirst()
                .orElse(null);

        if (gitRepository != null) {
            return gitRepository.getCurrentBranch().getName();
        }
        return StringUtils.EMPTY;
    }

    public boolean isTracking() {
        return startTime != null;
    }

    public void editCurrentSession() {
        Dimension smallFieldSize = new Dimension(150, 25);
        JTextField nameField = new JTextField(name);
        nameField.setPreferredSize(smallFieldSize);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);

        JTextArea descriptionArea = new JTextArea(description, 5, 20);
        descriptionArea.setFont(descriptionArea.getFont());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JBScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(300, 150));

        JPanel descriptionPanel = new JPanel(new BorderLayout(1, 1));
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, mainPanel, "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            setName(nameField.getText());
            setDescription(descriptionArea.getText());
        }
    }

    public void startTracking() {
        startTimer();
        componentsController.getStartTrackingButton().setEnabled(false);
        componentsController.getStopTrackingButton().setEnabled(true);
        componentsController.getEditCurrentSessionButton().setEnabled(true);
    }

    public void stopTracking() {
        stopTimer();
        componentsController.getStartTrackingButton().setEnabled(true);
        componentsController.getStopTrackingButton().setEnabled(false);
        componentsController.getEditCurrentSessionButton().setEnabled(false);
        JOptionPane.showMessageDialog(null, "Session has been stopped.");
    }
}