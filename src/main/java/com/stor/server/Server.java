package com.stor.server;

import com.stor.p2p.IStorApplication;
import com.stor.p2p.StorApplication;
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

import java.util.logging.Logger;

/**
 * User: rayje
 * Date: 10/13/13
 * Time: 9:46 PM
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final int port;

    private static IStorApplication application;
    public static IStorApplication getApplication() {
        return Server.application;
    }

    public static void main(String[] args) throws Exception {

        if(args.length != 1) {
            System.out.println("Usage: COMMAND <PASTRY_PORT_NUM>");
            System.exit(1);
        }

        Environment environment = new Environment();
        environment.getParameters().setString("nat_search_policy", "never");

        int port = 15080;
        int bindPort = Integer.parseInt(args[0]);

        //intialize the Application before accepting client commands
        Server.application = new StorApplication(bindPort, environment);

        //init server
        new Server(port).run();
    }

    public Server(int port) {
        this.port = port;
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
                                new ServerHandler());
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

