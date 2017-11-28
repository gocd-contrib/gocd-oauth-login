# Deprecation note
These plugins are deprecated as part of [GoCD release 17.5.0](https://www.gocd.org/releases/#17-5-0). Support for these plugins will be removed from 18.1.0 (scheduled to be released in January 2018). Replacement for these plugins are available at -

* [GitHub OAuth Authorization Plugin](https://github.com/gocd-contrib/github-oauth-authorization-plugin)
* [Google OAuth Authorization Plugin](https://github.com/gocd-contrib/google-oauth-authorization-plugin)
* [GitLab OAuth Authorization Plugin](https://github.com/gocd-contrib/gitlab-oauth-authorization-plugin)

# GoCD OAuth Login [![Build Status](https://snap-ci.com/gocd-contrib/gocd-oauth-login/branch/master/build_image)](https://snap-ci.com/gocd-contrib/gocd-oauth-login/branch/master)

This is GoCD's Authentication plugin that allows users to login using OAuth.

## Currently supported
* GitHub
* Google
* GitLab (CE)

## Adding new providers

The plugin internally uses [social-auth](https://github.com/3pillarlabs/socialauth) which acts as a wrapper for multiple OAuth integrations. Hence adding more integrations is very little effort. You will need to add a [provider](https://github.com/gocd-contrib/gocd-oauth-login/blob/master/src/main/java/com/tw/go/plugin/provider/Provider.java) and a [maven profile](https://github.com/gocd-contrib/gocd-oauth-login/blob/master/pom.xml#L65) (use **Google** implementation for reference).

## Requirements
* GoCD >= v15.2

## Getting Started

## Installation

Download the latest plugin jar from [Releases](https://github.com/gocd-contrib/gocd-oauth-login/releases) section. Place it in `<go-server-location>/plugins/external` and restart Go Server.

## Configuration

### Create an OAuth Application with an OAuth provider of your choice (GitHub, Google, etc.)

The oauth provider will normally ask you for an "OAuth Redirect URL". The redirect URL for the two plugins will be —
* Github OAuth Plugin -- `https://your-go-server/go/plugin/interact/github.oauth.login/authenticate`
* Google OAuth Plugin -- `https://your-go-server/go/plugin/interact/google.oauth.login/authenticate`
* GitLab OAuth Plugin -- `https://your-go-server/go/plugin/interact/gitlab.oauth.login/authenticate`

**Note:** We highly recommend that you use HTTPS for OAuth authorizations.

Once your application is registered, you will receive an "OAuth Client ID" and "OAuth Client Secret", save those for the next step.

### Configure the GoCD Server

**Note:** Due to a bug in the current version of GoCD, you'll need to set a valid Password file path under Server Configuration (or configure an LDAP server).

It is also recommended you have at least one local admin configured to avoid getting yourself locked out during this process. If you're using password files, make sure the file contains at least one entry.

On your go server, visit the plugin settings page, and enter those credentials.

On clicking save, you may be be logged out, if you're not logged out. Now is a good time to logout and see if you can get in.

The **GitHub** and **GitLab** plugins also supports user search, allowing you to search and add users right from GoCD's UI:
![Add User][7]

[7]: images/add-user.png  "Add User"

## Contributing

We encourage you to contribute to Go. For information on contributing to this project, please see our [contributor's guide](http://www.go.cd/contribute).
A lot of useful information like links to user documentation, design documentation, mailing lists etc. can be found in the [resources](http://www.go.cd/community/resources.html) section.

## License

```plain
Copyright 2015 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
