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

**NOTE**: The install scripts are written and tested to run on a Linux system.

To install the application on a Linux system, run the following command:

    $ curl -s https://raw.github.com/rayje/Stor/master/bin/install.sh | sudo sh

Once installation is complete, the following files will be installed:

    $ find /usr/local/stor
    /usr/local/stor
    /usr/local/stor/bin
    /usr/local/stor/bin/stor
    /usr/local/stor/bin/env.sh
    /usr/local/stor/bin/build
    /usr/local/stor/bin/install.sh
    /usr/local/stor/bin/server
    /usr/local/stor/bin/client
    /usr/local/stor/lib
    /usr/local/stor/lib/stor-1.0.jar

A symbolic link is created in <code>/usr/local/bin</code>

    $ ls -ltr /usr/local/bin/stor
    lrwxrwxrwx 1 root root 24 Oct 27 15:49 /usr/local/bin/stor -> /usr/local/stor/bin/stor

## Usage

After installation, the stor script installed at <code>/usr/local/stor/bin</code> can be used for user interaction
with the Stor application. The stor scripts provides options for interaction with both the client and the server.

The following is a list of options provided by the stor script:

    $ stor -h
    usage: /usr/local/bin/stor options

    This script is used to run the Stor server and client. The -c or -s option must
    be provided to start either the server or the client.

    OPTIONS:
        -h      Show this message
        -c      Start the client
        -s      Start the server

    SERVER OPTIONS:
        -p      The host address to be used by the Pastry server. This is required
                when using the -s option. This can be either the hostname or ip address.

    CLIENT OPTIONS:
        -a      The action to take (PUT, GET, DELETE).
        -i      The file id to be used with a GET action. Required when using the GET action.
        -f      The filename used for the PUT action. Required when using the PUT action.

### Server

To run the server run the following command:

    $ stor -s -p <application_ring_host>

```application_ring_host```: The hostname or ip address of the pastry ring server. To start a new application ring, specify the current server hostname.

### Client

The client takes a set of different options based on the action. When invoking the client, there are two main options,
PUT and GET.

##### ACTIONS

***PUT***

To run the PUT action, run the following command:

    $ stor -c -a PUT -f <filename>

```filename```: The name of the file you are trying to store.

***GET***

To run the GET action, run the following command:

    $ stor -c -a GET -i <fileid>

```fileid```: The id of the file returned after executing the PUT action

***STATUS***

To run the STATUS action, run the following command:

    $ stor -x


