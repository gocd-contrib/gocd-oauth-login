package com.tw.go.plugin.util;

import org.apache.commons.io.IOUtils;
import org.brickred.socialauth.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static com.tw.go.plugin.OAuthLoginPlugin.LOGGER;

public class Util {

    public static String pluginImage() {
        try (InputStream resourceAsStream = Util.class.getResourceAsStream("/logo.png")) {
            return "data:image/png;base64," + Base64.encodeBytes(IOUtils.toByteArray(resourceAsStream));
        } catch (IOException e) {
            LOGGER.error("Could not load plugin image", e);
            throw new RuntimeException(e);
        }
    }

    public static Class providerClass() throws ClassNotFoundException {
        return Class.forName(pluginProvider());
    }

    public static String pluginId() {
        String s = readResource("/defaults.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return (String) properties.get("pluginId");
        } catch (IOException e) {
            LOGGER.error("Could not determine plugin ID", e);
            throw new RuntimeException(e);
        }
    }

    public static String pluginProvider() {
        String s = readResource("/defaults.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return (String) properties.get("providerClass");
        } catch (IOException e) {
            LOGGER.error("Could not determine plugin provider", e);
            throw new RuntimeException(e);
        }
    }

    public static String readResource(String resourceFile) {
        try (InputStreamReader reader = new InputStreamReader(Util.class.getResourceAsStream(resourceFile), StandardCharsets.UTF_8)) {
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }
}
