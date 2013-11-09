ec2-client
----------
A NodeJS client for EC2.

Description
-----------
This client is a simple javascript application to create, start and stop EC2 instances.

Install
-------
The ```ec2``` script requires NodeJs to run.

Once NodeJs is installed, from the ec2 directory, you can run the following command to install the ec2 dependencies:

    $ npm install

Usage
-----

    $ ./ec2 -h

      Usage: ec2 [options]

      Options:

        -h, --help             output usage information
        -V, --version          output the version number
        --start <instanceIds>  A comma delimited list of instance ids
        --stop <instanceIds>   A comma delimited list of instance ids
        --launch <imageId>     Which reporter to use for displaying the results
        --name <instanceName>  A name to give to the instance being created. To be used with the --launch option
        --num <numInstances>   The number of instances to laaunch. To be used with the --launch option
        --size <sixe>          The size of the instance to laaunch (Default: t1.micro). To be used with the --launch option

Commands
--------

***start***

To start an existing instance run the following command:

    $ ./ec2 --start <instanceIds>

```instanceIds```: A comma delimited list of instance ids

***stop***

To stop a running EC2 instance, run the following command:

    $ ./ec2 --stop <instandeIds>

```instanceIds```: A comma delimited list of instance ids

***launch***

To launch/create an EC2 instance from a pre-existing AMI image.

    $ ./ec2 --launch <imageId>

```imageId```: The AMI image id.

The launch command also takes some additional options:

```name```: The name to give to the created instance.

```num```: The number of instances to create from the image (Default: 1).

```size```: The size of them instance to create (Default: t1.micro).