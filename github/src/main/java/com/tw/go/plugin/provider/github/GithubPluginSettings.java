package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.PluginSettings;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class GithubPluginSettings extends PluginSettings {
    private final List<String> githubOrganizations;
    private String username;
    private String password;
    private String oauthToken;
    private Boolean enterprise;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String apiUrl;

    public GithubPluginSettings(
            String serverBaseURL,
            String consumerKey,
            String consumerSecret,
            String username,
            String password,
            String oauthToken,
            String githubOrg,
            Boolean enterprise,
            String authorizeUrl,
            String accessTokenUrl,
            String apiUrl
    ) {
        super(serverBaseURL, consumerKey, consumerSecret);
        this.username = username;
        this.password = password;
        this.oauthToken = oauthToken;
        this.githubOrganizations = organizationsFromString(githubOrg);
        this.enterprise = enterprise;
        this.authorizeUrl = authorizeUrl;
        this.accessTokenUrl = accessTokenUrl;
        this.apiUrl = apiUrl;
    }

    private List<String> organizationsFromString(String orgs) {
        ArrayList<String> organizations = new ArrayList<>();

        if (StringUtils.isEmpty(orgs)) {
            return organizations;
        }

        for (String org : orgs.split(",")) {
            if (!StringUtils.isEmpty(org.trim())) {
                organizations.add(org);
            }
        }

        return organizations;
    }

    public boolean hasOrganizations() {
        return !this.githubOrganizations.isEmpty();
    }

    public List<String> getGithubOrganizations() {
        return githubOrganizations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public boolean containsUsernameAndPassword() {
        return !isBlank(username) && !isBlank(password);
    }

    public boolean containsOAuthToken() {
        return !isBlank(oauthToken);
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
