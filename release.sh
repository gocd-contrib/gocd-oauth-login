#!/bin/sh
rm -rf dist/
mkdir dist

mvn clean install -DskipTests -P github.pr.status
cp target/github-oauth-*.jar dist/

mvn clean install -DskipTests -P stash.pr.status
cp target/google-oauth-*.jar dist/

