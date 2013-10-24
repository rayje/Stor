Stor
====

A P2P Storage prototype

## Building the app

The Stor application uses Maven to compile and package the application. The 'build' script is provided to assist in
compiling and packaging the Stor application.

### Dependencies

The Stor application has two main dependencies.

* [netty 4.0.9](http://netty.io/)
* [freepastry 2.1](http://www.freepastry.org/)

It is expected that these dependencies be installed in the local Maven repository in order for the build script to
successfully compile and package the Stor application.

### Building

To build the application, execute the following command:

    $ ./build

## Usage

There are helper scripts provided to assist in running the client and server applications.

### Server

To run the server run the following command:

    $ ./server <port>

#### Arguments:
- port: The port to be used by the freepastry server.

### Client

To run the client, execute the following command:

    $ ./client

