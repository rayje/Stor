#!/bin/sh


if [ "$(id -u)" != "0" ]; then
    echo "This script must be run as root" 1>&2
    exit 1
fi

wget -q https://raw.github.com/rayje/Stor/master/tools/env.sh -P /tmp
. /tmp/env.sh
rm /tmp/env.sh

if [ "$JAVA_HOME" = "" ]; then
    echo "Could not find java. Aborting."
    exit 1
else 
    echo "Found java at $JAVA_HOME"
fi

if ! type mvn > /dev/null; then
    echo "Maven is required but not installed. Aborting."
    exit 1
fi

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
        wget "$PASTRY_URL/$PASTRY_TAR" -P $LOCAL_PASTRY
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

    mvn compile
}

trap on_exit EXIT

log "Created tmp: $TMP"
cd $TMP

clone_stor
install_pastry
compile_stor

