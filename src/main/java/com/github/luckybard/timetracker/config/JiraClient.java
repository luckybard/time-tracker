package com.github.luckybard.timetracker.config;

import com.intellij.openapi.project.Project;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JiraClient {

    private static final OkHttpClient client = new OkHttpClient();
    private Project project;

    public JiraClient(@NotNull Project project) {
        this.project= project;
    }

    public PluginProperties getSettings(){
        return project.getService(PluginProperties.class);
    }

    public boolean sendJiraUpdate(String issueKey, String timeSpent, String comment) {
        try {
            // Wysyłanie zarówno aktualizacji czasu, jak i komentarza
            boolean timeUpdated = updateIssueTime(issueKey, timeSpent);
            boolean commentAdded = addCommentToIssue(issueKey, comment);

            return timeUpdated && commentAdded; // Zwraca true, jeśli obie operacje zakończą się sukcesem
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateIssueTime(String issueKey, String timeSpent) {
        String jsonBody = "{"
                + "\"timeSpent\":\"" + timeSpent + "\""
                + "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(getSettings().getJiraUrl() + "/rest/api/2/issue/" + issueKey + "/worklog")
                .header("Authorization", "Basic " + getAuthHeader())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean addCommentToIssue(String issueKey, String comment) {
        String jsonBody = "{"
                + "\"body\":\"" + comment + "\""
                + "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(getSettings().getJiraUrl() + "/rest/api/2/issue/" + issueKey + "/comment")
                .header("Authorization", "Basic " + getAuthHeader())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getAuthHeader() {
        String credentials = getSettings().getUsername() + ":" + getSettings().getApiToken();
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}