package com.stor.server;

import com.stor.p2p.StorApplication;
import com.stor.p2p.StorApplicationImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import rice.environment.Environment;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: rayje
 * Date: 10/13/13
 * Time: 9:46 PM
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    //local application server port. this port is used by the client to connect to and send commands to the server
    private static final int SERVER_PORT = 15080;

    //pastry ring port number. this port number is where an pastry node will boot to
    private static final int PASTRY_RING_PORT = 17373;


    private final int port;
    private StorApplication application;

    public static void main(String[] args) throws Exception {

        if(args.length != 1) {
            System.out.println("Usage: COMMAND <PASTRY_RING_HOST_NAME>");
            System.out.println("Eg: To initialize a Pastry Ring: MYHOSTNAME");
            System.out.println("Eg: To join a Pastry Ring: RING_HOSTNAME");
            System.exit(1);
        }

        try {
            Environment environment = new Environment();
            environment.getParameters().setString("nat_search_policy", "never");

            //for testing. allow starting a pastry node on localhost
            environment.getParameters().setString("pastry_socket_allow_lookback", "true");

            //initialize the boot address for the pastry ring.
            InetSocketAddress bootAddress = new InetSocketAddress(InetAddress.getByName(args[0]), PASTRY_RING_PORT);

            StorApplication application;
            //intialize the Application before accepting client commands
            application = new StorApplicationImpl(PASTRY_RING_PORT, bootAddress, environment);
            //init server
            new Server(SERVER_PORT, application).run();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Application Error", ex);
            System.out.println("Application Error. Reason: " + ex.getMessage());
            System.exit(1);
        }
    }

    public Server(int port, StorApplication application) {
        this.port = port;
        this.application = application;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                new ServerHandler(application));
                    }
                });

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

