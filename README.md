# GoCD OAuth Login

This is GoCD's Authentication plugin that allows users to login using OAuth.

## Currently supported
* GitHub
* Google

## Adding new providers

The plugin internally uses [social-auth](https://github.com/3pillarlabs/socialauth) which acts as a wrapper for multiple OAuth integrations. Hence adding more integrations is very little effort. You will need to add a [provider](https://github.com/gocd-contrib/gocd-oauth-login/blob/master/src/main/java/com/tw/go/plugin/provider/Provider.java) and a [maven profile](https://github.com/gocd-contrib/gocd-oauth-login/blob/master/pom.xml#L65) (use **Google** implementation for reference).

## Requirements
* GoCD >= v15.2

## Getting Started

## Installation

Download the latest plugin jar from [Releases](https://github.com/gocd-contrib/gocd-oauth-login/releases) section. Place it in `<go-server-location>/plugins/external` and restart Go Server.

## Configuration

### Create an OAuth Application with an OAuth provider of your choice (GitHub, Google, etc.)

The oauth provider will normally ask you for an "OAuth Redirect URL". The redirect URL will be `https://your-go-server/go/plugin/interact/github.oauth.login/authenticate`.

**Note:** We highly recommend that you use HTTPS for OAuth authorizations.

Once your application is registered, you will receive an "OAuth Client ID" and "OAuth Client Secret", save those for the next step.

### Configure the GoCD Server

**Note:** Due to a bug in the current version of GoCD, you'll need to set a valid Password file path under Server Configuration (or configure an LDAP server).

It is also recommended you have at least one local admin configured to avoid getting yourself locked out during this process. If you're using password files, make sure the file contains at least one entry.

On your go server, visit the plugin settings page, and enter those credentials.

On clicking save, you may be be logged out, if you're not logged out. Now is a good time to logout and see if you can get in.

The **GitHub** plugin also supports user search, allowing you to search and add users right from GoCD's UI:
![Add User][7]

[7]: images/add-user.png  "Add User"
