#!/bin/sh
#==============================================================================
# This script will setup FreePastry as a Maven project, compile and install
# it in the local Maven repository so that is can be used as a dependency
# in other Maven projects.
#==============================================================================

MY_HOME=${MY_HOME:-$HOME}
STOR_TOOLS_DIR=$(pwd)
M2_REPO=${M2_REPO:-$MY_HOME/.m2/repository}
PASTRY_DIR=${PASTRY_DIR:-/tmp/stor.9NpF8sSOrr/pastry-2.1}
UPNPLIB_REPO=$M2_REPO/net/sbbi/sbbi-upnplib/1.0.4

#==============================================
# Add sbbi-upnplib to local repository
#==============================================
if [ ! -d $UPNPLIB_REPO ]; then
    mkdir -p $UPNPLIB_REPO
    cd $UPNPLIB_REPO
    wget http://repo.karatachi.org/mvn/net/sbbi/sbbi-upnplib/1.0.4/sbbi-upnplib-1.0.4.jar
    echo "UpnpLib installed at $UPNPLIB_REPO"

    echo "\n==============================================================="
    if [ -f $UPNPLIB_REPO/sbbi-upnplib-1.0.4.jar ]; then
        echo "UpnpLib successfully added to the Maven Repository"
    else
        echo "An problem occurred while trying to add UpnpLib to the Maven Repository"
        exit 1
    fi
    echo "===============================================================\n"
else
    echo "\n==============================================================="
    echo "UpnpLib is already installed at $UPNPLIB_REPO"
    echo "===============================================================\n"
fi

#==============================================
# Setup freepastry
#
# This assumes freepastry has already been
# downloaded and PASTRY_DIR is set to the
# directory that contains the freepastry
# source code
#==============================================
if [ ! -d $M2_REPO/pastry ]; then
    cd $STOR_TOOLS_DIR
    cp pastry.pom $PASTRY_DIR/pom.xml

    cd $PASTRY_DIR
    mvn -X install

    echo "\n==============================================================="
    if [ -f $M2_REPO/pastry/pastry/2.1/pastry-2.1.jar ]; then
        echo "FreePastry successfully added to the Maven Repository"
    else
        echo "An problem occurred while trying to add FreePastry to the Maven Repository"
    fi
    echo "==============================================================="

else
    echo "\n==============================================================="
    echo "FreePastry is already installed as a Maven Repository"
    echo "==============================================================="
fi

