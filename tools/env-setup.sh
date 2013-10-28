#!/bin/sh

sudo apt-get install openjdk-7-jdk git maven curl
sudo update-alternatives --config java

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/