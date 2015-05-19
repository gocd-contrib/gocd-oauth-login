package com.tw.go.plugin;

public class PluginSettings {
    private String serverBaseURL;
    private String consumerKey;
    private String consumerSecret;

    public PluginSettings(String serverBaseURL, String consumerKey, String consumerSecret) {
        this.serverBaseURL = serverBaseURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getServerBaseURL() {
        return serverBaseURL;
    }

    public void setServerBaseURL(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        if (consumerKey != null ? !consumerKey.equals(that.consumerKey) : that.consumerKey != null) return false;
        if (consumerSecret != null ? !consumerSecret.equals(that.consumerSecret) : that.consumerSecret != null)
            return false;
        if (serverBaseURL != null ? !serverBaseURL.equals(that.serverBaseURL) : that.serverBaseURL != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverBaseURL != null ? serverBaseURL.hashCode() : 0;
        result = 31 * result + (consumerKey != null ? consumerKey.hashCode() : 0);
        result = 31 * result + (consumerSecret != null ? consumerSecret.hashCode() : 0);
        return result;
    }
}
