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

- You will see `Github OAuth Login` / `Google OAuth Login` on plugin listing page
![Plugins listing page][1]

- You will need to generate consumer key & consumer secret to configure the plugin
![Configure plugin pop-up][2]

- Acess `<go-server>/go/plugin/interact/github.oauth.login/index` from browser

- You will be asked to authorize application to access your data
![GitHub authorize page][3]

- And done!
![Pipeline Dashboard][4]

- `GitHub` plugin supports user search. So you can search users & add them.
![Add User][4]

[1]: images/list-plugin.png  "List Plugin"
[2]: images/configure-plugin.png  "Configure Plugin"
[3]: images/github-login.png  "Authorize GitHub Login"
[4]: images/successful-login.png  "On Successful Login"
[4]: images/add-user.png  "Add User"
