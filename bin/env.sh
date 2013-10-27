#!/bin/sh

export MY_HOME=$(getent passwd $(whoami) | cut -d: -f6)
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/

NAME=StorInstall
STOR_REPO=https://github.com/rayje/Stor.git
PASTRY_URL=http://www.freepastry.org/FreePastry
PASTRY_TAR=FreePastry-2.1-source.tgz
LOCAL_PASTRY=/tmp/pastry
export MY_HOME=$(getent passwd $(whoami) | cut -d: -f6)
