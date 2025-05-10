package com.github.luckybard.timetracker.service;

import com.github.luckybard.timetracker.model.Session;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

import static com.github.luckybard.timetracker.util.TimeUtils.getDurationAsString;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Service(Service.Level.PROJECT)
public final class JiraService {

    private static final Logger logger = LoggerFactory.getLogger(JiraService.class);

    public static final String WORKLOG_ENDPOINT = "/worklog";
    public static final String COMMENT_ENDPOINT = "/comment";

    private static final OkHttpClient client = new OkHttpClient();
    private final PropertiesService propertiesService;

    public JiraService(@NotNull Project project) {
        this.propertiesService = project.getService(PropertiesService.class);
    }

    public boolean sendSessionToJira(Session session) {
        logger.info("JiraService::sendSessionToJira()");
        try {
            String timeSpent = getDurationAsString(session.getDuration());
            String comment = generateComment(session, timeSpent);
            return sendJiraUpdate(getIssueKey(session), timeSpent, comment);
        } catch (Exception e) {
            logger.error("Failed to send session to Jira", e);
            return false;
        }
    }

    private boolean sendJiraUpdate(String issueKey, String timeSpent, String comment) {
        try {
            return updateIssueTime(issueKey, timeSpent) && addCommentToIssue(issueKey, comment);
        } catch (IOException e) {
            System.err.println("Error while updating Jira: " + e.getMessage());
            return false;
        }
    }

    private boolean updateIssueTime(String issueKey, String timeSpent) throws IOException {
        String jsonBody = createJsonBody("timeSpent", timeSpent);
        Request request = buildRequest(issueKey, WORKLOG_ENDPOINT, jsonBody);

        return executeRequest(request);
    }

    private boolean addCommentToIssue(String issueKey, String comment) throws IOException {
        String jsonBody = createJsonBody("body", comment);
        Request request = buildRequest(issueKey, COMMENT_ENDPOINT, jsonBody);

        return executeRequest(request);
    }

    private String createJsonBody(String key, String value) {
        return "{" + "\"" + key + "\":\"" + value + "\"}";
    }

    private Request buildRequest(String issueKey, String endpoint, String jsonBody) {
        return new Request.Builder()
                .url(propertiesService.getJiraUrl() + "/rest/api/2/issue/" + issueKey + endpoint)
                .header("Authorization", "Basic " + getAuthHeader())
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();
    }

    private boolean executeRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAuthHeader() {
        String credentials = propertiesService.getJiraUsername() + ":" + propertiesService.getJiraApiToken();
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }


    private String getIssueKey(Session session) {
        return propertiesService.getJiraProjectKey() + "-" +
                session.getBranch().replaceAll("^[^0-9]*([0-9]+).*", "$1");
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
}
