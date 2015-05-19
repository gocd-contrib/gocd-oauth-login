package com.tw.go.plugin.provider;

import com.tw.go.plugin.User;
import org.brickred.socialauth.Profile;

public interface Provider {
    public String getPluginId();

    public String getName();

    public String getProviderName();

    public String getConsumerKeyPropertyName();

    public String getConsumerSecretPropertyName();

    public User getUser(Profile profile);
}
