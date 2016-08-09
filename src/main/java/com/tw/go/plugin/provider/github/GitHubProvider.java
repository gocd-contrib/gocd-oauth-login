package com.tw.go.plugin.provider.github;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.ImageReader;
import org.apache.commons.lang.StringUtils;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GitHubProvider implements Provider {
    private static final String IMAGE = ImageReader.readImage("GitHub-Mark-Light-64px.png");
    private static Logger LOGGER = Logger.getLoggerFor(GitHubProvider.class);

    @Override
    public String getPluginId() {
        return "github.oauth.login";
    }

    @Override
    public String getName() {
        return "GitHub";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public String getProviderName() {
        return "github";
    }

    @Override
    public Permission getAuthPermission() {
        return Permission.CUSTOM;
    }

    @Override
    public User getUser(Profile profile) {
        String displayName = profile.getDisplayName();
        String fullName = profile.getFullName();
        String emailId = profile.getEmail();
        return new User(displayName, fullName, emailId);
    }

    @Override
    public List<User> searchUser(PluginSettings pluginSettings, String searchTerm) {
        List<User> users = new ArrayList<User>();
        try {
            GitHub github = getGitHub(pluginSettings);

            PagedSearchIterable<GHUser> githubSearchResults = github.searchUsers().q(searchTerm).list();
            int count = 0;
            for (GHUser githubSearchResult : githubSearchResults) {
                users.add(new User(githubSearchResult.getLogin(), githubSearchResult.getName(), githubSearchResult.getEmail()));
                count++;

                if (count == 10) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to perform user search", e);
        }
        return users;
    }

    @Override
    public boolean authorize(PluginSettings pluginSettings, User user) {
        if (StringUtils.isEmpty(pluginSettings.getUsernameRegex())) {
            return true;
        } else {
            return isAMemberOfOrganization(pluginSettings, user);
        }
    }

    @Override
    public Properties configure(PluginSettings pluginSettings) {
        Properties properties = new Properties();
        properties.put("api.github.com.consumer_key", pluginSettings.getConsumerKey());
        properties.put("api.github.com.consumer_secret", pluginSettings.getConsumerSecret());
        properties.put("api.github.com.custom_permissions", "read:org, user:email");
        return properties;
    }

    private boolean isAMemberOfOrganization(PluginSettings pluginSettings, User user) {
        boolean result = false;
        try {
            GitHub github = getGitHub(pluginSettings);

            GHOrganization organization = github.getOrganization(pluginSettings.getUsernameRegex());
            GHUser ghUser = github.getUser(user.getUsername());

            result = ghUser.isMemberOf(organization);
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to check if user is member of organization", e);
        }

        return result;
    }

    private GitHub getGitHub(PluginSettings pluginSettings) throws IOException {
        GitHub github = null;
        if (pluginSettings.containsUsernameAndPassword()) {
            github = GitHub.connectUsingPassword(pluginSettings.getUsername(), pluginSettings.getPassword());
        } else if (pluginSettings.containsOAuthToken()) {
            github = GitHub.connectUsingOAuth(pluginSettings.getOauthToken());
        }
        if (github == null) {
            throw new RuntimeException("Plugin not configured. Please provide plugin settings.");
        }
        return github;
    }
}
