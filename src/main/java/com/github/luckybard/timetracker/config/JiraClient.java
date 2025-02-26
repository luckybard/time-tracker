package com.github.luckybard.timetracker.config;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Base64;

@Service(Service.Level.PROJECT)
public final class JiraClient {

    public static final String WORKLOG_ENDPOINT = "/worklog";
    public static final String COMMENT_ENDPOINT = "/comment";

    private static final OkHttpClient client = new OkHttpClient();
    private final Project project;

    public JiraClient(@NotNull Project project) {
        this.project= project;
    }

    public PluginProperties getSettings(){
        return project.getService(PluginProperties.class);
    }

    public boolean sendJiraUpdate(String issueKey, String timeSpent, String comment) {
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
                .url(getSettings().getJiraUrl() + "/rest/api/2/issue/" + issueKey + endpoint)
                .header("Authorization", "Basic " + getAuthHeader())
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();
    }

    private boolean executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAuthHeader() {
        String credentials = getSettings().getUsername() + ":" + getSettings().getApiToken();
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
