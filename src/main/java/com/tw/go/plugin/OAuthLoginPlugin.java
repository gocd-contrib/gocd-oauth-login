package com.tw.go.plugin;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.FieldValidator;
import com.tw.go.plugin.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.brickred.socialauth.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.Arrays.asList;

@Extension
public class OAuthLoginPlugin implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(OAuthLoginPlugin.class);

    public static final String EXTENSION_NAME = "authentication";
    private static final List<String> goSupportedVersions = asList("1.0");

    public static final String PLUGIN_SETTINGS_SERVER_BASE_URL = "server_base_url";
    public static final String PLUGIN_SETTINGS_CONSUMER_KEY = "consumer_key";
    public static final String PLUGIN_SETTINGS_CONSUMER_SECRET = "consumer_secret";
    public static final String PLUGIN_SETTINGS_USERNAME = "username";
    public static final String PLUGIN_SETTINGS_PASSWORD = "password";
    public static final String PLUGIN_SETTINGS_OAUTH_TOKEN = "oauth_token";
    public static final String PLUGIN_SETTINGS_USERNAME_REGEX = "username_regex";

    public static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String PLUGIN_CONFIGURATION = "go.authentication.plugin-configuration";
    public static final String SEARCH_USER = "go.authentication.search-user";
    public static final String WEB_REQUEST_INDEX = "index";
    public static final String WEB_REQUEST_AUTHENTICATE = "authenticate";

    public static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";
    public static final String GO_REQUEST_SESSION_PUT = "go.processor.session.put";
    public static final String GO_REQUEST_SESSION_GET = "go.processor.session.get";
    public static final String GO_REQUEST_SESSION_REMOVE = "go.processor.session.remove";
    public static final String GO_REQUEST_AUTHENTICATE_USER = "go.processor.authentication.authenticate-user";

    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int REDIRECT_RESPONSE_CODE = 302;
    public static final int UNAUTHORIZED_RESPONSE_CODE = 401;
    public static final int NOT_FOUND_ERROR_RESPONSE_CODE = 404;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    private Provider provider;
    private GoApplicationAccessor goApplicationAccessor;

    public OAuthLoginPlugin() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/defaults.properties"));
            Class<?> providerClass = Class.forName(properties.getProperty("provider"));
            Constructor<?> constructor = providerClass.getConstructor();
            provider = (Provider) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("could not create provider", e);
        }
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        String requestName = goPluginApiRequest.requestName();
        if (requestName.equals(PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
                return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
            }
        } else if (requestName.equals(PLUGIN_SETTINGS_VALIDATE_CONFIGURATION)) {
            return handleValidatePluginSettingsConfiguration(goPluginApiRequest);
        } else if (requestName.equals(PLUGIN_CONFIGURATION)) {
            Map<String, Object> configuration = getPluginConfiguration();
            return renderJSON(SUCCESS_RESPONSE_CODE, configuration);
        } else if (requestName.equals(SEARCH_USER)) {
            return handleSearchUserRequest(goPluginApiRequest);
        } else if (requestName.equals(WEB_REQUEST_INDEX)) {
            return handleSetupLoginWebRequest(goPluginApiRequest);
        } else if (requestName.equals(WEB_REQUEST_AUTHENTICATE)) {
            return handleAuthenticateWebRequest(goPluginApiRequest);
        }
        return renderJSON(NOT_FOUND_ERROR_RESPONSE_CODE, null);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return getGoPluginIdentifier();
    }

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_CONSUMER_KEY, createField("Consumer Key", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_CONSUMER_SECRET, createField("Consumer Secret", null, true, false, "2"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
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

    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("template", IOUtils.toString(getClass().getResourceAsStream("/plugin-settings.template.html"), "UTF-8"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> responseMap = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());
        final Map<String, String> configuration = keyValuePairs(responseMap, "plugin-settings");
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "server_base_url", "Server Base URL");
            }
        });

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "consumer_key", "Consumer Key");
            }
        });

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "consumer_secret", "Consumer Secret");
            }
        });

        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private void validate(List<Map<String, Object>> response, FieldValidator fieldValidator) {
        Map<String, Object> fieldValidation = new HashMap<String, Object>();
        fieldValidator.validate(fieldValidation);
        if (!fieldValidation.isEmpty()) {
            response.add(fieldValidation);
        }
    }

    private void validateRequiredField(Map<String, String> configuration, Map<String, Object> fieldMap, String key, String name) {
        if (configuration.get(key) == null || configuration.get(key).isEmpty()) {
            fieldMap.put("key", key);
            fieldMap.put("message", String.format("'%s' is a required field", name));
        }
    }

    private Map<String, Object> getPluginConfiguration() {
        Map<String, Object> configuration = new HashMap<String, Object>();
        configuration.put("display-name", provider.getName());
        configuration.put("display-image-url", provider.getImageURL());
        configuration.put("supports-web-based-authentication", true);
        configuration.put("supports-password-based-authentication", false);
        return configuration;
    }

    private GoPluginApiResponse handleSearchUserRequest(GoPluginApiRequest goPluginApiRequest) {
        Map<String, String> requestBodyMap = (Map<String, String>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());
        String searchTerm = requestBodyMap.get("search-term");
        PluginSettings pluginSettings = getPluginSettings();
        List<User> users = provider.searchUser(pluginSettings, searchTerm);
        if (users == null || users.isEmpty()) {
            return renderJSON(SUCCESS_RESPONSE_CODE, null);
        } else {
            List<Map> searchResults = new ArrayList<Map>();
            for (User user : users) {
                searchResults.add(getUserMap(user));
            }
            return renderJSON(SUCCESS_RESPONSE_CODE, searchResults);
        }
    }

    private GoPluginApiResponse handleSetupLoginWebRequest(GoPluginApiRequest goPluginApiRequest) {
        try {
            PluginSettings pluginSettings = getPluginSettings();
            Properties oauthConsumerProperties = new Properties();
            oauthConsumerProperties.put(provider.getConsumerKeyPropertyName(), pluginSettings.getConsumerKey());
            oauthConsumerProperties.put(provider.getConsumerSecretPropertyName(), pluginSettings.getConsumerSecret());
            SocialAuthConfig socialAuthConfiguration = SocialAuthConfig.getDefault();
            socialAuthConfiguration.load(oauthConsumerProperties);
            SocialAuthManager manager = new SocialAuthManager();
            manager.setSocialAuthConfig(socialAuthConfiguration);
            String redirectURL = manager.getAuthenticationUrl(provider.getProviderName(), getURL(pluginSettings.getServerBaseURL()), Permission.AUTHENTICATE_ONLY);

            store(manager);

            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Location", redirectURL);
            return renderJSON(REDIRECT_RESPONSE_CODE, responseHeaders, null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while OAuth setup.", e);
            return renderJSON(INTERNAL_ERROR_RESPONSE_CODE, null);
        }
    }

    public PluginSettings getPluginSettings() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", provider.getPluginId());
        GoApiResponse response = goApplicationAccessor.submit(createGoApiRequest(GET_PLUGIN_SETTINGS, JSONUtils.toJSON(requestMap)));
        if (response.responseBody() == null || response.responseBody().trim().isEmpty()) {
            throw new RuntimeException("plugin is not configured. please provide plugin settings.");
        }
        Map<String, String> responseBodyMap = (Map<String, String>) JSONUtils.fromJSON(response.responseBody());
        return new PluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL), responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET), responseBodyMap.get(PLUGIN_SETTINGS_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_PASSWORD), responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN),
                responseBodyMap.get(PLUGIN_SETTINGS_USERNAME_REGEX)
        );
    }

    private GoPluginApiResponse handleAuthenticateWebRequest(final GoPluginApiRequest goPluginApiRequest) {
        try {
            PluginSettings pluginSettings = getPluginSettings();
            SocialAuthManager manager = read();
            if (manager == null) {
                throw new RuntimeException("socialauth manager not set");
            }

            AuthProvider authProvider = manager.connect(goPluginApiRequest.requestParameters());
            Profile profile = authProvider.getUserProfile();
            User user = provider.getUser(profile);

            if (provider.authorize(pluginSettings, user)) {
                authenticateUser(user);
            }

            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Location", pluginSettings.getServerBaseURL());
            return renderJSON(REDIRECT_RESPONSE_CODE, responseHeaders, null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while OAuth authenticate.", e);
            return renderJSON(INTERNAL_ERROR_RESPONSE_CODE, null);
        } finally {
            delete();
        }
    }

    private void store(SocialAuthManager socialAuthManager) {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", provider.getPluginId());
        Map<String, Object> sessionData = new HashMap<String, Object>();
        String socialAuthManagerStr = serializeObject(socialAuthManager);
        sessionData.put("social-auth-manager", socialAuthManagerStr);
        requestMap.put("session-data", sessionData);
        GoApiRequest goApiRequest = createGoApiRequest(GO_REQUEST_SESSION_PUT, JSONUtils.toJSON(requestMap));
        GoApiResponse response = goApplicationAccessor.submit(goApiRequest);
        // handle error
    }

    private String serializeObject(SocialAuthManager socialAuthManager) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(socialAuthManager);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new String(Base64.encodeBase64(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SocialAuthManager read() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", provider.getPluginId());
        GoApiRequest goApiRequest = createGoApiRequest(GO_REQUEST_SESSION_GET, JSONUtils.toJSON(requestMap));
        GoApiResponse response = goApplicationAccessor.submit(goApiRequest);
        // handle error
        String responseBody = response.responseBody();
        Map<String, String> sessionData = (Map<String, String>) JSONUtils.fromJSON(responseBody);
        String socialAuthManagerStr = sessionData.get("social-auth-manager");
        return deserializeObject(socialAuthManagerStr);
    }

    private SocialAuthManager deserializeObject(String socialAuthManagerStr) {
        try {
            byte bytes[] = Base64.decodeBase64(socialAuthManagerStr.getBytes());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (SocialAuthManager) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void delete() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", provider.getPluginId());
        GoApiRequest goApiRequest = createGoApiRequest(GO_REQUEST_SESSION_REMOVE, JSONUtils.toJSON(requestMap));
        GoApiResponse response = goApplicationAccessor.submit(goApiRequest);
        // handle error
    }

    private void authenticateUser(User user) {
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("user", getUserMap(user));
        GoApiRequest authenticateUserRequest = createGoApiRequest(GO_REQUEST_AUTHENTICATE_USER, JSONUtils.toJSON(userMap));
        GoApiResponse authenticateUserResponse = goApplicationAccessor.submit(authenticateUserRequest);
        // handle error
    }

    private String getURL(String serverBaseURL) {
        return String.format("%s/go/plugin/interact/%s/authenticate", serverBaseURL, provider.getPluginId());
    }

    private Map<String, String> getUserMap(User user) {
        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username", user.getUsername());
        userMap.put("display-name", user.getDisplayName());
        userMap.put("email-id", user.getEmailId());
        return userMap;
    }

    private Map<String, String> keyValuePairs(Map<String, Object> map, String mainKey) {
        Map<String, String> keyValuePairs = new HashMap<String, String>();
        Map<String, Object> fieldsMap = (Map<String, Object>) map.get(mainKey);
        for (String field : fieldsMap.keySet()) {
            Map<String, Object> fieldProperties = (Map<String, Object>) fieldsMap.get(field);
            String value = (String) fieldProperties.get("value");
            keyValuePairs.put(field, value);
        }
        return keyValuePairs;
    }

    private GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoApiRequest createGoApiRequest(final String api, final String responseBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "1.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return getGoPluginIdentifier();
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return responseBody;
            }
        };
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        return renderJSON(responseCode, null, response);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, final Map<String, String> responseHeaders, Object response) {
        final String json = response == null ? null : JSONUtils.toJSON(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return responseHeaders;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}
