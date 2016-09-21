package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.User;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GoogleProviderTest {

    @Test
    public void authorizationWithAllowedDomains() throws Exception {
        GooglePluginSettings googlePluginSettings = new GooglePluginSettings();
        googlePluginSettings.setAllowedDomains("foo.com, bar.com");

        assertFalse(new GoogleProvider().authorize(googlePluginSettings, new User("", null, null)));
        assertFalse(new GoogleProvider().authorize(googlePluginSettings, new User("bob@foo.com@example.com", null, null)));

        assertTrue(new GoogleProvider().authorize(googlePluginSettings, new User("bob@foo.com", null, null)));
        assertTrue(new GoogleProvider().authorize(googlePluginSettings, new User("bob@bar.com", null, null)));
    }

    @Test
    public void authorizationWithRegex() throws Exception {
        GooglePluginSettings googlePluginSettings = new GooglePluginSettings();
        googlePluginSettings.setUsernameRegex(".*@(foo.com|bar.com)$");

        assertFalse(new GoogleProvider().authorize(googlePluginSettings, new User("", null, null)));
        assertFalse(new GoogleProvider().authorize(googlePluginSettings, new User("bob@foo.com@example.com", null, null)));

        assertTrue(new GoogleProvider().authorize(googlePluginSettings, new User("bob@foo.com", null, null)));
        assertTrue(new GoogleProvider().authorize(googlePluginSettings, new User("bob@bar.com", null, null)));
    }
}
