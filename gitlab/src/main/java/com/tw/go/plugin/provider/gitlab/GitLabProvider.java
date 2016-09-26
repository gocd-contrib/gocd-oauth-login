package com.tw.go.plugin.provider.gitlab;

import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.Util;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.tw.go.plugin.OAuthLoginPlugin.*;

public class GitLabProvider implements Provider<GitLabPluginSettings> {
    private static final String IMAGE = Util.pluginImage();
    private static final String PLUGIN_ID = Util.pluginId();
    private static final String PLUGIN_SETTINGS_GITLAB_BASE_URL = "gitlab_base_url";

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
        return new ArrayList<>();
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
        properties.put("gitlab.access_token_url", pluginSettings.getGitlabBaseUrl() + "/oauth/token");
        properties.put("gitlab.authentication_url", pluginSettings.getGitlabBaseUrl() + "/oauth/authorize");
        properties.put("gitlab.custom.profile_url", pluginSettings.getGitlabBaseUrl() + "/api/v3/user");
        properties.put("gitlab.custom_permissions", "api");
        return properties;
    }

    @Override
    public GitLabPluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        return new GitLabPluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET), responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                responseBodyMap.get(PLUGIN_SETTINGS_GITLAB_BASE_URL)
        );
    }
}
