package com.tw.go.plugin.provider.gitlab;

import com.tw.go.plugin.PluginSettings;

public class GitLabPluginSettings extends PluginSettings {
    private String serverBaseURL;
    private String consumerKey;
    private String consumerSecret;
    private String oauthToken;
    private String gitlabBaseUrl;

    public GitLabPluginSettings(String serverBaseURL, String consumerKey, String consumerSecret, String oauthToken, String gitlabBaseUrl) {
        super(serverBaseURL, consumerKey, consumerSecret);
        this.oauthToken = oauthToken;
        this.gitlabBaseUrl = gitlabBaseUrl;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public String getGitlabBaseUrl() {
        return gitlabBaseUrl;
    }
}
