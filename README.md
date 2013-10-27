Stor
====

A P2P Storage prototype

## Building the app

The Stor application uses Maven to compile and package the application. The <code>build</code> script is provided to assist in
compiling and packaging the Stor application.

### Dependencies

The Stor application has two main dependencies.

* [netty 4.0.9](http://netty.io/)
* [freepastry 2.1](http://www.freepastry.org/)

It is expected that these dependencies be installed in the local Maven repository in order for the build script to
successfully compile and package the Stor application.

### Building

To build the application, execute the following command:

    $ ./bin/build

## Installing the app

NOTE: The install scripts are written and tested to run on a Linux system.

To install the application on a Linux system, run the following command:

    $ curl -s https://raw.github.com/rayje/Stor/master/bin/install.sh | sudo sh

This will install all the dependencies for the Stor application and install Stor in <code>/usr/local/Stor</code>

## Usage

There are helper scripts provided to assist in running the client and server applications.

### Server

To run the server run the following command:

    $ ./bin/server <application_ring_hostname>

#### Arguments:
- application_ring_hostname: The hostname of the pastry ring server. To start a new application ring, specify the current server hostname.

### Client

To run the client, execute the following command:

    $ ./bin/client

