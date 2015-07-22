# GoCD OAuth Login
This is GoCD's Authentication plugin that allows users to login using OAuth.

Supported:
* GitHub
* Google

Adding support for more providers:

The plugin internally uses [social-auth](https://github.com/3pillarlabs/socialauth) which acts as a wrapper for multiple OAuth integrations. Hence adding more integrations is very little effort. 
You will need to add a [provider](https://github.com/srinivasupadhya/gocd-oauth-login/blob/master/src/main/java/com/tw/go/plugin/provider/Provider.java) & a [maven profile](https://github.com/srinivasupadhya/gocd-oauth-login/blob/master/pom.xml#L65) (use `Google` implementation for reference).

## Requirements
This needs GoCD >= v15.2 which is due release as of writing.

## Get Started
**Installation:**
- Download the latest plugin jar from [Releases](https://github.com/srinivasupadhya/gocd-oauth-login/releases) section. Place it in `<go-server-location>/plugins/external` & restart Go Server.

## Behavior

- Generate OAuth Client ID & Client Secret.
![Generate OAuth Token][1]

- Generate "Personal access token" (optional - if you do not want to use username & password).

- You will see `Github OAuth Login` / `Google OAuth Login` on plugin listing page
![Plugins listing page][2]

- You will need to generate consumer key & consumer secret to configure the plugin
![Configure plugin pop-up][3]

![Login Page][4]

- You will be asked to authorize application to access your data
![GitHub authorize page][5]

- And done!
![Pipeline Dashboard][6]

- `GitHub` plugin supports user search. So you can search users & add them.
![Add User][7]

[1]: images/generate-oauth-token.png  "Generate OAuth Token"
[2]: images/list-plugin.png  "List Plugin"
[3]: images/configure-plugin.png  "Configure Plugin"
[4]: images/login-page.png  "Login Page"
[5]: images/github-login.png  "Authorize GitHub Login"
[6]: images/successful-login.png  "On Successful Login"
[7]: images/add-user.png  "Add User"
