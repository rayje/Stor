#!/bin/sh
#==========================================================
# The install script will install all the dependencies
# required by Stor. Installation involves the following
# steps:
#   1. Download Stor source code.
#   2. Download FreePastry
#   3. Download and install UpnpLib into local Maven repo
#   4. Compile and Install FreePastry to local Maven repo
#   5. Compile Stor
#   6. Install Stor at /usr/local/stor
#==========================================================


if [ "$(id -u)" != "0" ]; then
    echo "This script must be run as root" 1>&2
    exit 1
fi

if ! type mvn > /dev/null; then
    echo "Maven is required but not installed. Aborting."
    exit 1
fi

STOR_INSTALL_DIR=${STOR_INSTALL_DIR:-/usr/local/Stor}
STOR_REPO=${STOR_REPO:-https://github.com/rayje/Stor.git}
NAME=StorInstall
TMP=$(mktemp -d /tmp/stor.XXXXXXXXXX) || { echo "Failed to create temp dir"; exit 1; }
export PASTRY_DIR=$TMP/pastry-2.1

log() {
    logger -p notice $1 -t $NAME -s
}

on_exit() {
    if [ -d $TMP ]; then
        rm -rf $TMP
        log "Deleted $TMP"
    fi
}

clone_stor() {
    log "Cloning $STOR_REPO into $TMP"
    git clone -q $STOR_REPO 
}

install_pastry() {
    if [ ! -d $LOCAL_PASTRY ]; then
        mkdir $LOCAL_PASTRY
    fi

    if [ ! -f $LOCAL_PASTRY/$PASTRY_TAR ]; then
        wget "$PASTRY_URL" -P $LOCAL_PASTRY
        if [ ! -f $LOCAL_PASTRY/$PASTRY_TAR ]; then
            log "Unable to get $PASTRY_URL/$PASTRY_TAR. Aborting"
            exit 1
        fi
    fi

    tar -xf $LOCAL_PASTRY/$PASTRY_TAR -C $TMP
    if [ ! -d $TMP/pastry-2.1 ]; then
        log "Unable to extract $TMP/$PASTRY_TAR. Aborting."
        exit 1
    fi

    cd $PASTRY_DIR
    mkdir -p src/main/java
    cd src
    mv org rice $PASTRY_DIR/src/main/java/

    cd $TMP/Stor/tools/
    ./pastry-setup.sh

    rc=$?
    if [ $rc != 0 ] ; then
        log "Error installing Pastry. Aborting."
        exit $rc
    fi
}

compile_stor() {
    cd $TMP/Stor 

    if [ ! -f "pom.xml" ]; then
        log "Error: $TMP/Stor/pom.xml does not exist. Aborting"
        exit 1
    fi

    mvn clean compile assembly:single
}

install_stor() {
    if [ ! -d $STOR_INSTALL_DIR ]; then
        mkdir -p $STOR_INSTALL_DIR
    else
        STOR_BAK=/tmp/stor.$(date +"%m_%d_%Y_%H_%M_%S").tar
        tar -cf $STOR_BAK $STOR_INSTALL_DIR
        log "Created backup of previous Stor install at $STOR_BAK"
    fi
 
    cp -R $TMP/Stor/bin $STOR_INSTALL_DIR
    chmod 755 $STOR_INSTALL_DIR/bin/*

    if [ ! -h /usr/local/bin/stor ]; then
        ln -s $STOR_INSTALL_DIR/bin/stor /usr/local/bin/stor
    fi

    if [ ! -d $STOR_INSTALL_DIR/lib ]; then
        mkdir -p $STOR_INSTALL_DIR/lib
    fi

    cp $TMP/Stor/target/stor-1.0-jar-with-dependencies.jar $STOR_INSTALL_DIR/lib/stor-1.0.jar
}

trap on_exit EXIT

log "Created tmp: $TMP"
cd $TMP

clone_stor

if [ ! -f $TMP/Stor/bin/env.sh ]; then
    log "Unable to source Stor environment. Aborting"
    exit 1
fi

. $TMP/Stor/bin/env.sh

if [ "$JAVA_HOME" = "" ]; then
    echo "Could not find java. Aborting."
    exit 1
else 
    echo "Found java at $JAVA_HOME"
fi

install_pastry
compile_stor
install_stor

log "Stor successfully installed"
