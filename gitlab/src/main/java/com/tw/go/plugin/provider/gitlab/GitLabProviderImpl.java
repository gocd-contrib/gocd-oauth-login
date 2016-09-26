package com.tw.go.plugin.provider.gitlab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.brickred.socialauth.*;
import org.brickred.socialauth.exception.AccessTokenExpireException;
import org.brickred.socialauth.exception.ServerDataException;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.exception.UserDeniedPermissionException;
import org.brickred.socialauth.oauthstrategy.OAuth2;
import org.brickred.socialauth.oauthstrategy.OAuthStrategyBase;
import org.brickred.socialauth.util.*;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.*;

public class GitLabProviderImpl extends AbstractProvider {
    private final Log LOG = LogFactory.getLog(this.getClass());

    private final OAuthConfig config;
    private Permission scope;
    private AccessGrant accessGrant;
    private OAuthStrategyBase authenticationStrategy;
    private Profile userProfile;

    public GitLabProviderImpl(final OAuthConfig providerConfig) throws Exception {
        this.config = providerConfig;
        if(this.config.getCustomPermissions() != null) {
            this.scope = Permission.CUSTOM;
        }

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("authorizationURL", this.config.getAuthenticationUrl());
        endpoints.put("accessTokenURL", this.config.getAccessTokenUrl());

        this.authenticationStrategy = new OAuth2(this.config, endpoints);
        this.authenticationStrategy.setPermission(this.scope);
        this.authenticationStrategy.setScope(this.getScope());
    }

    @Override
    protected List<String> getPluginsList() {
        return new ArrayList<>();
    }

    @Override
    protected OAuthStrategyBase getOauthStrategy() {
        return authenticationStrategy;
    }

    @Override
    public String getLoginRedirectURL(final String successUrl) throws Exception {
        return authenticationStrategy.getLoginRedirectURL(successUrl);
    }

    @Override
    public Profile verifyResponse(final Map<String, String> requestParams) throws Exception {
        return doVerifyResponse(requestParams);
    }

    private Profile doVerifyResponse(final Map<String, String> requestParams)
            throws Exception {
        LOG.info("Retrieving Access Token in verify response function");
        if (requestParams.get("error_reason") != null && "user_denied".equals(requestParams.get("error_reason"))) {
            throw new UserDeniedPermissionException();
        }
        accessGrant = authenticationStrategy.verifyResponse(requestParams, MethodType.POST.toString());

        if (accessGrant != null) {
            LOG.debug("Obtaining user profile");
            return getProfile();
        } else {
            throw new SocialAuthException("Access token not found");
        }
    }

    @Override
    public Response updateStatus(final String msg) throws Exception {
        throw notSupported("Update Status");
    }

    @Override
    public List<Contact> getContactList() throws Exception {
        throw notSupported("Get Contact List");
    }

    @Override
    public Profile getUserProfile() throws Exception {
        if (userProfile == null && accessGrant != null) {
            getProfile();
        }
        return userProfile;
    }

    private Profile getProfile() throws Exception {
        String response;

        String profileUrl = config.getCustomProperties().get("profile_url");
        try {
            response = authenticationStrategy.executeFeed(profileUrl).getResponseBodyAsString(Constants.ENCODING);
        } catch (Exception e) {
            throw new SocialAuthException("Error while getting profile from " + profileUrl, e);
        }
        try {
            LOG.debug("User Profile : " + response);
            JSONObject resp = new JSONObject(response);
            Profile p = new Profile();
            p.setValidatedId(resp.optString("id", null));
            p.setFullName(resp.optString("name", null));
            p.setEmail(resp.optString("email", null));
            p.setDisplayName(resp.optString("login", null));
            p.setProviderId(getProviderId());
            if (config.isSaveRawResponse()) {
                p.setRawResponse(response);
            }
            userProfile = p;
            return userProfile;
        } catch (Exception ex) {
            throw new ServerDataException(
                    "Failed to parse the user profile json : " + response, ex);
        }
    }

    @Override
    public void logout() {
        accessGrant = null;
        authenticationStrategy.logout();
    }

    @Override
    public void setPermission(Permission permission) {
        LOG.debug("Permission requested : " + permission.toString());
        this.scope = permission;
        authenticationStrategy.setPermission(this.scope);
        authenticationStrategy.setScope(getScope());
    }


    @Override
    public Response api(final String url, final String methodType,
                        final Map<String, String> params,
                        final Map<String, String> headerParams, final String body)
            throws Exception {
        LOG.info("Calling api function for url	:	" + url);
        try {
            return authenticationStrategy.executeFeed(url, methodType, params, headerParams, body);
        } catch (Exception e) {
            throw new SocialAuthException("Error while making request to URL : " + url, e);
        }
    }

    @Override
    public AccessGrant getAccessGrant() {
        return accessGrant;
    }

    @Override
    public String getProviderId() {
        return config.getId();
    }

    @Override
    public void setAccessGrant(final AccessGrant accessGrant)
            throws AccessTokenExpireException, SocialAuthException {
        this.accessGrant = accessGrant;
        authenticationStrategy.setAccessGrant(accessGrant);

    }

    @Override
    public Response uploadImage(final String message, final String fileName,
                                final InputStream inputStream) throws Exception {
        throw notSupported("Upload Image");
    }

    private SocialAuthException notSupported(String action) {
        LOG.warn("WARNING: Not implemented for GitHub");
        return new SocialAuthException(String.format("%s is not implemented for GitHub", action));
    }

    private String getScope() {
        return "api";
    }
}
