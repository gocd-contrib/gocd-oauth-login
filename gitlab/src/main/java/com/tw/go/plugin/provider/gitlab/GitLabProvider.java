package com.tw.go.plugin.provider.gitlab;

import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.JSONUtils;
import com.tw.go.plugin.util.Util;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;

import java.io.IOException;
import java.util.*;

import static com.tw.go.plugin.OAuthLoginPlugin.*;

public class GitLabProvider implements Provider<GitLabPluginSettings> {
    private static final String IMAGE = Util.pluginImage();
    private static final String PLUGIN_ID = Util.pluginId();
    private static final String PLUGIN_SETTINGS_GITLAB_BASE_URL = "gitlab_base_url";
    private OkHttpClient client = new OkHttpClient();

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String getName() {
        return "GitLab";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public String getProviderName() {
        return "gitlab";
    }

    @Override
    public Permission getAuthPermission() {
        return Permission.CUSTOM;
    }

    @Override
    public User getUser(Profile profile) {
        String fullName = profile.getFullName();
        String emailId = profile.getEmail();
        return new User(emailId, fullName, emailId);
    }

    @Override
    public List<User> searchUser(GitLabPluginSettings pluginSettings, String searchTerm) {
        HttpUrl searchBaseUrl = HttpUrl.parse(fullUrl(pluginSettings, "/api/v3/users"));

        HttpUrl url = new HttpUrl.Builder()
                .scheme(searchBaseUrl.scheme())
                .host(searchBaseUrl.host())
                .addPathSegments(searchBaseUrl.encodedPath())
                .addQueryParameter("private_token", pluginSettings.getOauthToken())
                .addQueryParameter("search", searchTerm)
                .build();

        Request request = new Request.Builder().url(url.url()).build();

        try {
            Response response = client.newCall(request).execute();
            List<User> users = new ArrayList<>();

            for (Map<String, String> userParams : (List<Map<String, String>>) JSONUtils.fromJSON(response.body().string())) {
                users.add(new User(userParams.get("email"), userParams.get("name"), userParams.get("email")));
            }
            return users;

        } catch (IOException e) {
            LOGGER.warn("Error occurred while trying to perform user search", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean authorize(GitLabPluginSettings pluginSettings, User user) {
        return true;
    }

    @Override
    public Properties configure(GitLabPluginSettings pluginSettings) {
        Properties properties = new Properties();
        properties.put("socialauth.gitlab", "com.tw.go.plugin.provider.gitlab.GitLabProviderImpl");
        properties.put("gitlab.consumer_key", pluginSettings.getConsumerKey());
        properties.put("gitlab.consumer_secret", pluginSettings.getConsumerSecret());
        properties.put("gitlab.access_token_url", fullUrl(pluginSettings, "/oauth/token"));
        properties.put("gitlab.authentication_url", fullUrl(pluginSettings, "/oauth/authorize"));
        properties.put("gitlab.custom.profile_url", fullUrl(pluginSettings, "/api/v3/user"));
        properties.put("gitlab.custom_permissions", "api");
        return properties;
    }

    private String fullUrl(GitLabPluginSettings pluginSettings, String path) {
        return pluginSettings.getGitlabBaseUrl() + path;
    }

    @Override
    public GitLabPluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        return new GitLabPluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET), responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                responseBodyMap.get(PLUGIN_SETTINGS_GITLAB_BASE_URL)
        );
    }

    @Override
    public Map<String, Object> handleGetPluginSettings() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_CONSUMER_KEY, createField("OAuth Client ID", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_CONSUMER_SECRET, createField("OAuth Client Secret", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_GITLAB_BASE_URL, createField("GitLab Server Base URL", null, true, false, "3"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("GitLab Server Base URL", null, false, false, "4"));
        return response;
    }

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
        Map<String, Object> fieldProperties = new HashMap<String, Object>();
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        return fieldProperties;
    }
}
