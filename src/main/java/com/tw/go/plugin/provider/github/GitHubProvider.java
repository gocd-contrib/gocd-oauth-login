package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import org.brickred.socialauth.Profile;

public class GitHubProvider implements Provider {
    @Override
    public String getPluginId() {
        return "github.oauth.login";
    }

    @Override
    public String getName() {
        return "GitHub";
    }

    @Override
    public String getProviderName() {
        return "github";
    }

    @Override
    public String getConsumerKeyPropertyName() {
        return "api.github.com.consumer_key";
    }

    @Override
    public String getConsumerSecretPropertyName() {
        return "api.github.com.consumer_secret";
    }

    @Override
    public User getUser(Profile profile) {
        String displayName = profile.getDisplayName();
        String fullName = profile.getFullName();
        String emailId = profile.getEmail();
        return new User(displayName, fullName, emailId);
    }
}
