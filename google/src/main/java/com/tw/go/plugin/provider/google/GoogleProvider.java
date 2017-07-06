package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.User;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.Util;
import org.brickred.socialauth.Permission;
import org.brickred.socialauth.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.tw.go.plugin.OAuthLoginPlugin.*;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class GoogleProvider implements Provider<GooglePluginSettings> {

    private static final String IMAGE = Util.pluginImage();
    private static final String PLUGIN_ID = Util.pluginId();

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
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
    public List<User> searchUser(GooglePluginSettings pluginSettings, String searchTerm) {
        return null;
    }

    @Override
    public boolean authorize(GooglePluginSettings pluginSettings, User user) {
        String username = user.getUsername();

        if (isNotBlank(pluginSettings.getAllowedDomains())) {
            String allowedDomains = pluginSettings.getAllowedDomains();
            String[] domains = allowedDomains.split(",");
            for (String domain : domains) {
                if (username.endsWith("@" + domain.trim())) {
                    return true;
                }
            }
            return false;
        }
        if (isNotBlank(pluginSettings.getUsernameRegex())) {
            try {
                return username.matches(pluginSettings.getUsernameRegex().trim());
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Properties configure(GooglePluginSettings pluginSettings) {
        Properties properties = new Properties();
        properties.put("googleapis.com.consumer_key", pluginSettings.getConsumerKey());
        properties.put("googleapis.com.consumer_secret", pluginSettings.getConsumerSecret());
        properties.put("www.google.com.custom_permissions", "https://www.googleapis.com/auth/userinfo.email,https://www.googleapis.com/auth/userinfo.profile,https://www.googleapis.com/auth/plus.me");

        return properties;
    }

    @Override
    public GooglePluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        return new GooglePluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL), responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET),
                responseBodyMap.get(PLUGIN_SETTINGS_USERNAME_REGEX),
                responseBodyMap.get(PLUGIN_SETTINGS_ALLOWED_DOMAINS)
        );
    }

    @Override
    public Map<String, Object> handleGetPluginSettings() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_CONSUMER_KEY, createField("OAuth Client ID", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_CONSUMER_SECRET, createField("OAuth Client Secret", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_ALLOWED_DOMAINS, createField("List of google domains that should be allowed to login", null, false, false, "3"));
        return response;
    }

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
        Map<String, Object> fieldProperties = new HashMap<String, Object>();
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        return fieldProperties;
    }

}
