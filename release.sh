#!/bin/sh
rm -rf dist/
mkdir dist

mvn clean install -DskipTests -P github.oauth.login
cp target/github-oauth-*.jar dist/

mvn clean install -DskipTests -P google.oauth.login
cp target/google-oauth-*.jar dist/

