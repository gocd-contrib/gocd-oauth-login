package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.PluginSettings;

public class GithubPluginSettings extends PluginSettings {
    private final String githubOrg;

    public GithubPluginSettings(String serverBaseURL, String consumerKey, String consumerSecret, String username, String password, String oauthToken, String githubOrg) {
        super(serverBaseURL, consumerKey, consumerSecret, username, password, oauthToken);
        this.githubOrg = githubOrg;
    }

    public String getGithubOrg() {
        return githubOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GithubPluginSettings that = (GithubPluginSettings) o;

        return githubOrg != null ? githubOrg.equals(that.githubOrg) : that.githubOrg == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (githubOrg != null ? githubOrg.hashCode() : 0);
        return result;
    }
}
