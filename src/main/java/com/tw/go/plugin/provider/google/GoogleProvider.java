package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.RegexUtils;
import org.brickred.socialauth.Profile;

import java.util.List;

public class GoogleProvider implements Provider {
    @Override
    public String getPluginId() {
        return "google.oauth.login";
    }

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public String getImageURL() {
        return "http://icons.iconarchive.com/icons/sicons/basic-round-social/48/google-icon.png";
    }

    @Override
    public String getProviderName() {
        return "googleplus";
    }

    @Override
    public String getConsumerKeyPropertyName() {
        return "googleapis.com.consumer_key";
    }

    @Override
    public String getConsumerSecretPropertyName() {
        return "googleapis.com.consumer_secret";
    }

    @Override
    public User getUser(Profile profile) {
        String emailId = profile.getEmail();
        String fullName = profile.getFullName();
        return new User(emailId, fullName, emailId);
    }

    @Override
    public List<User> searchUser(PluginSettings pluginSettings, String searchTerm) {
        return null;
    }

    @Override
    public boolean authorize(PluginSettings pluginSettings, User user) {
        return RegexUtils.matchesRegex(user.getUsername(), pluginSettings.getUsernameRegex());
    }
}
