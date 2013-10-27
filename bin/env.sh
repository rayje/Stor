#!/bin/sh

export MY_HOME=$(getent passwd $(whoami) | cut -d: -f6)
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

PASTRY_TAR=FreePastry-2.1-source.tgz
PASTRY_URL=http://www.freepastry.org/FreePastry/$PASTRY_TAR
LOCAL_PASTRY=/tmp/pastry
