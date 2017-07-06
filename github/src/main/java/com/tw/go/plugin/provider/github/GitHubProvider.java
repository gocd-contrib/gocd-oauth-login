package com.tw.go.plugin.provider.github;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.Util;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.*;

import static com.tw.go.plugin.OAuthLoginPlugin.*;

public class GitHubProvider implements Provider<GithubPluginSettings> {
    private static final String IMAGE = Util.pluginImage();
    private static final String PLUGIN_ID = Util.pluginId();

    private static Logger LOGGER = Logger.getLoggerFor(GitHubProvider.class);

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
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
    public List<User> searchUser(GithubPluginSettings pluginSettings, String searchTerm) {
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
    public boolean authorize(GithubPluginSettings pluginSettings, User user) {
        if(pluginSettings.hasOrganizations()){
            return isAMemberOfOrganization(pluginSettings, user);
        }
        return true;
    }

    @Override
    public Properties configure(GithubPluginSettings pluginSettings) {
        Properties properties = new Properties();
        properties.put("api.github.com.consumer_key", pluginSettings.getConsumerKey());
        properties.put("api.github.com.consumer_secret", pluginSettings.getConsumerSecret());
        if (pluginSettings.isEnterprise()) {
            properties.put("api.github.com.authentication_url", pluginSettings.getAuthorizeUrl());
            properties.put("api.github.com.access_token_url", pluginSettings.getAccessTokenUrl());
            properties.put("api.github.com.custom.apiURL", pluginSettings.getApiUrl());
        }
        if (pluginSettings.hasOrganizations()) {
            properties.put("api.github.com.custom_permissions", "user:email, read:org");
        } else {
            properties.put("api.github.com.custom_permissions", "user:email");
        }
        return properties;
    }

    @Override
    public GithubPluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        return new GithubPluginSettings(
                responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL), responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET), responseBodyMap.get(PLUGIN_SETTINGS_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_PASSWORD), responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                responseBodyMap.get(PLUGIN_SETTINGS_ORG_NAME), responseBodyMap.containsKey(PLUGIN_SETTINGS_ENTERPRISE),
                responseBodyMap.get(PLUGIN_SETTINGS_AUTHORIZE_URL), responseBodyMap.get(PLUGIN_SETTINGS_ACCESS_TOKEN_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_API_URL)
        );
    }

    @Override
    public Map<String, Object> handleGetPluginSettings() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_CONSUMER_KEY, createField("OAuth Client ID", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_CONSUMER_SECRET, createField("OAuth Client Secret", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, false, false, "3"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, false, true, "4"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, false, true, "5"));
        response.put(PLUGIN_SETTINGS_ORG_NAME, createField("GitHub Organization Name(s) (requires you to provide a username and either a password or oauth token)", null, false, false, "10"));
        response.put(PLUGIN_SETTINGS_ENTERPRISE, createField("GitHub Enterprise", "false", false, false, "6"));
        response.put(PLUGIN_SETTINGS_AUTHORIZE_URL, createField("GitHub Authorization Url", null, false, false, "7"));
        response.put(PLUGIN_SETTINGS_ACCESS_TOKEN_URL, createField("GitHub Authorization Url", null, false, false, "8"));
        response.put(PLUGIN_SETTINGS_API_URL, createField("GitHub API Url", null, false, false, "9"));

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

    private boolean isAMemberOfOrganization(GithubPluginSettings pluginSettings, User user) {
        try {
            GitHub github = getGitHub(pluginSettings);
            GHUser ghUser = github.getUser(user.getUsername());

            if(ghUser == null) return false;

            for(String orgName: pluginSettings.getGithubOrganizations()) {
                GHOrganization organization = github.getOrganization(orgName);

                if(organization != null && ghUser.isMemberOf(organization)){
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to check if user is member of organization", e);
        }
        return false;
    }

    private GitHub getGitHub(GithubPluginSettings pluginSettings) throws IOException {
        GitHub github = null;
        // Connect to either enterprise github server or public github
        if (pluginSettings.isEnterprise()) {
            github = connectToEnterpriseGitHub(pluginSettings);
        } else {
            github = connectToPublicGitHub(pluginSettings);
        }

        if (github == null) {
            throw new RuntimeException("Plugin not configured. Please provide plugin settings.");
        }
        return github;
    }

    private GitHub connectToPublicGitHub(GithubPluginSettings pluginSettings) throws IOException {
        if (pluginSettings.containsUsernameAndPassword()) {
            LOGGER.debug("Create GitHub connection to public GitHub using username and password");
            return GitHub.connectUsingPassword(pluginSettings.getUsername(), pluginSettings.getPassword());
        } else if (pluginSettings.containsOAuthToken()) {
            LOGGER.debug("Create GitHub connection to public GitHub with token");
            return GitHub.connectUsingOAuth(pluginSettings.getOauthToken());
        }
        return null;
    }

    private GitHub connectToEnterpriseGitHub(GithubPluginSettings pluginSettings) throws IOException {
        if (pluginSettings.containsUsernameAndPassword()) {
            LOGGER.debug("Create GitHub connection to enterprise GitHub using username and password");
            return GitHub.connectToEnterprise(
                    pluginSettings.getApiUrl(),
                    pluginSettings.getUsername(),
                    pluginSettings.getPassword()
            );
        } else if (pluginSettings.containsOAuthToken()) {
            LOGGER.debug("Create GitHub connection to enterprise GitHub with token");
            return GitHub.connectToEnterprise(pluginSettings.getApiUrl(), pluginSettings.getOauthToken());
        }
        return null;
    }
}
