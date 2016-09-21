package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.PluginSettings;

public class GooglePluginSettings extends PluginSettings {
    @Deprecated
    private String usernameRegex;
    private String allowedDomains;

    public GooglePluginSettings(String serverBaseURL, String consumerKey, String consumerSecret, String username, String password, String oauthToken, String usernameRegex, String allowedDomains) {
        super(serverBaseURL, consumerKey, consumerSecret, username, password, oauthToken);
        this.usernameRegex = usernameRegex;
        this.allowedDomains = allowedDomains;
    }

    public GooglePluginSettings() {
        super();
    }

    public String getAllowedDomains() {
        return allowedDomains;
    }

    @Deprecated
    public String getUsernameRegex() {
        return usernameRegex;
    }

    @Deprecated
    public void setUsernameRegex(String usernameRegex) {
        this.usernameRegex = usernameRegex;
    }

    public void setAllowedDomains(String allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GooglePluginSettings that = (GooglePluginSettings) o;

        if (usernameRegex != null ? !usernameRegex.equals(that.usernameRegex) : that.usernameRegex != null)
            return false;
        return allowedDomains != null ? allowedDomains.equals(that.allowedDomains) : that.allowedDomains == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (usernameRegex != null ? usernameRegex.hashCode() : 0);
        result = 31 * result + (allowedDomains != null ? allowedDomains.hashCode() : 0);
        return result;
    }
}
