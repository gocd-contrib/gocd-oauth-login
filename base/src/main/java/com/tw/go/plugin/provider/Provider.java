package com.tw.go.plugin.provider;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.User;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface Provider<T extends PluginSettings> {
    String getPluginId();

    String getName();

    String getImageURL();

    String getProviderName();

    Permission getAuthPermission();

    User getUser(Profile profile);

    List<User> searchUser(T pluginSettings, String searchTerm);

    boolean authorize(T pluginSettings, User user);

    Properties configure(T pluginSettings);

    T pluginSettings(Map<String, String> responseBodyMap);
}
