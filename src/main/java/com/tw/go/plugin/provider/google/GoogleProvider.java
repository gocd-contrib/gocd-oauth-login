package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.ImageReader;
import com.tw.go.plugin.util.RegexUtils;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;

import java.util.List;
import java.util.Properties;

public class GoogleProvider implements Provider {

    private static final String IMAGE = ImageReader.readImage("logo_google_plus_64px.png");

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
        return IMAGE;
    }

    @Override
    public String getProviderName() {
        return "googleplus";
    }

    @Override
    public Permission getAuthPermission() {
        return Permission.AUTHENTICATE_ONLY;
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

    @Override
    public Properties configure(PluginSettings pluginSettings) {
        Properties properties = new Properties();
        properties.put("googleapis.com.consumer_key", pluginSettings.getConsumerKey());
        properties.put("googleapis.com.consumer_secret", pluginSettings.getConsumerSecret());
        properties.put("www.google.com.custom_permissions", "https://www.googleapis.com/auth/userinfo.email,https://www.googleapis.com/auth/userinfo.profile,https://www.googleapis.com/auth/plus.me");

        return properties;
    }
}
