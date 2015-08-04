package com.tw.go.plugin.provider;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.User;
import org.brickred.socialauth.Profile;

import java.util.List;

public interface Provider {
    public String getPluginId();

    public String getName();

    public String getImageURL();

    public String getProviderName();

    public String getConsumerKeyPropertyName();

    public String getConsumerSecretPropertyName();

    public User getUser(Profile profile);

    public List<User> searchUser(PluginSettings pluginSettings, String searchTerm);

    public boolean authorize(PluginSettings pluginSettings, User user);
}
