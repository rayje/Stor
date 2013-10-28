package com.stor.client;

import com.stor.commands.Command;
import com.stor.commands.GetCommand;
import com.stor.commands.PutCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class Client {

    private final String host;
    private final int port;
    private Command command;

    private static final int NUMARGS = 2;  //expected number of arguments

    public static void main(String[] args) throws Exception {

        if (args.length != NUMARGS) QuitOnError();

        final String host = "127.0.0.1";
        final int port = 15080;
        final String cmd = args[0].toUpperCase();
        final String fileName = args[1];

        if (!cmd.equals("PUT") && !cmd.equals("GET")) {
            QuitOnError();
        }

        System.out.println("Command accepted: " + cmd + " file: " + fileName);
        new Client(host, port, cmd, fileName).run();
    }

    public Client(String host, int port, String cmd, String fName) {
        this.host = host;
        this.port = port;

        if (cmd.equals("PUT")) {
            this.command = new PutCommand(fName);
        } else if (cmd.equals("GET")) {
            this.command = new GetCommand(fName);
        }
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ClientHandler(command));
                        }
                    });

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    // private helper - quit and print error message
    private static void QuitOnError() {
        System.err.println("Usage: Client <PUT> <fileName> or <GET> <fileId>");
        System.exit(1);
    }

}
